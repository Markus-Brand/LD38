package mbeb.opengldefault.rendering.io;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import org.joml.*;
import org.lwjgl.assimp.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;

/**
 * Contains logic to create Renderables from Files
 */
public class ObjectLoader {

	private static final DataFragment[] PosNormUv = new DataFragment[] {
			DataFragment.POSITION,
			DataFragment.NORMAL,
			DataFragment.TANGENT,
			DataFragment.UV
	};
	private static final DataFragment[] PosNormUvAnim3 = new DataFragment[] {
			DataFragment.POSITION,
			DataFragment.NORMAL,
			DataFragment.TANGENT,
			DataFragment.UV,
			DataFragment.BONE_INDICES_3,
			DataFragment.BONE_WEIGHTS_3
	};
	private static final float THRESHOLD = 0.2f; //vertices with smaller weights are not included into the boundingBox
	//todo test for good values here

	private static final String TAG = "ObjectLoader";

	/**
	 * load an object into a Renderable. Assumes, that the file contains 3
	 * Position, 3 Normal and 2 UV Fragments
	 *
	 * @param path
	 *            the absolute File-Path to the object
	 * @return a VAO-Renderable
	 */
	public IRenderable loadFromFile(String path) {
		return loadFromFile(path, PosNormUv);
	}

	/**
	 * also load animation data (3 ids / 3 weights)
	 *
	 * @param path
	 *            the absolute File-Path to the object
	 * @return a Renderable
	 */
	public AnimatedMesh loadFromFileAnim(String path) {
		AnimatedMesh mesh =  (AnimatedMesh)loadFromFile(path, PosNormUvAnim3);
		loadAnimationPriorities(mesh, path + ".weights.yaml");
		return mesh;
	}

	/**
	 * load the priorities of movements per bone
	 * @param mesh the mesh to modify
	 * @param weightsPath path to the file containing this information
	 */
	private void loadAnimationPriorities(AnimatedMesh mesh, String weightsPath) {
		String extractedPath = getExtractedPath(weightsPath);
		if (extractedPath == null) {
			return;
		}
		YAMLParser.YAMLNode root = new YAMLParser(new File(extractedPath)).getRoot();
		for (YAMLParser.YAMLNode animNode: root.getChildren()) {
			Animation anim = mesh.getAnimationByName(animNode.getName());

			if (anim != null) {
				for (YAMLParser.YAMLNode boneNode : animNode.getChildren()) {
					adjustBoneAnimationPriorities(anim, anim.getSkeleton(), boneNode);
				}
			}
		}
	}

	/**
	 * apply one directive (one line of YAML) to the corresponding Animation
	 * @param anim the Animation to alter
	 * @param bone the root bone (anim.getSkeleton())
	 * @param boneNode the directive to apply
	 */
	private void adjustBoneAnimationPriorities(Animation anim, Bone bone, YAMLParser.YAMLNode boneNode) {
		if (bone.getName().toLowerCase().contains(boneNode.getName().toLowerCase())) {
			bone.foreach((Bone ancestor) ->
					anim.setBonePriority(ancestor, Integer.valueOf(boneNode.getData())));
		} else {
			for (Bone child: bone.getChildren()) {
				adjustBoneAnimationPriorities(anim, child, boneNode);
			}
		}
	}

	/**
	 * load an object into a Renderable
	 *
	 * @param path
	 *            the absolute File-Path to the object
	 * @param format
	 *            which data to load
	 * @return a VAO-Renderable
	 */
	public IRenderable loadFromFile(String path, DataFragment[] format) {

		String realPath = getExtractedPath(path);
		AIScene scene = Assimp.aiImportFile(realPath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace);

		Bone sceneStructure = parseScene(scene);

		if (scene.mNumMeshes() > 0) {
			return loadMesh(scene, 0, format, sceneStructure);
			//todo not return just the first mesh, rather combine meshes
		} else {
			Log.error(TAG, "No Mesh found in object");
			return null;
		}
	}

	private String getExtractedPath(String rawPath) {
		File res = new File("res");
		if (!res.exists()) {
			res.mkdirs();
		}
		File export = new File(res, rawPath);
		if (!export.exists()) {
			try {
				InputStream inStream = GLContext.class.getResourceAsStream("/models/" + rawPath);
				if (inStream == null) {
					return null;
				}
				Files.copy(inStream, export.toPath());
			} catch(IOException ex) {
				Log.error(TAG, "Cannot extract resource " + rawPath, ex);
			}
		}
		return "res/" + rawPath;
	}

	/**
	 * load a single mesh
	 *
	 * @param scene
	 *            the AIScene to load from
	 * @param meshID
	 *            which mesh to load
	 * @param format
	 *            the format the data should be in
	 * @return
	 */
	private IRenderable loadMesh(AIScene scene, int meshID, DataFragment[] format, Bone sceneStructure) {
		AIMesh mesh = AIMesh.create(scene.mMeshes().get(meshID));

		int vertexCount = mesh.mNumVertices();

		VAORenderable vaomesh = new VAORenderable(vertexCount, format);
		GLBufferWriter dataWriter = vaomesh.dataWriter();

		BoundingBox box = new BoundingBox.Empty();
		boolean isAnimated = DataFragment.needsBoneData(format);
		// vertex-id  3elements     bone-id  weight
		Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights = null;
		Bone skeleton = null;

		if (isAnimated) {
			skeleton = parseSkeleton(mesh, sceneStructure);
			vertexBoneWeights = loadVertexWeights(mesh, skeleton, 3);
		}
		Matrix4f sceneTransform = BoneTransformation.matrixFromAI(scene.mRootNode().mTransformation());

		for (int v = 0; v < vertexCount; v++) {
			AIVector3D aiposition = mesh.mVertices().get(v);
			Vector3f position = new Vector3f(aiposition.x(), aiposition.y(), aiposition.z());
			box = box.extendTo(position);
			if (isAnimated) {
				adjustBoneBoxes(skeleton, position, v, vertexBoneWeights, sceneTransform);
			}
			for (DataFragment dataFormat : format) {
				dataFormat.addTo(mesh, v, dataWriter, vertexBoneWeights);
			}
		}
		mesh.close();

		dataWriter.flush(GLBufferWriter.WriteType.FULL_DATA);
		vaomesh.setAttribPointers();
		vaomesh.setBoundingBox(box);

		if (isAnimated) {
			AnimatedMesh animMesh = new AnimatedMesh(vaomesh, skeleton);
			animMesh.setTransform(sceneTransform);
			loadAnimations(animMesh, scene);
			return animMesh;
		} else {
			return vaomesh;
		}
	}

	/**
	 * load the per-vertex weights on the bones
	 *
	 * @param mesh
	 * @param skeleton
	 * @param weightsAmount
	 *            how many bones per vertex
	 * @return
	 */
	private Map<Integer, List<Map.Entry<Integer, Float>>> loadVertexWeights(AIMesh mesh, Bone skeleton, int weightsAmount) {
		//calculate weights here
		Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights = new HashMap<>();
		Map<Integer, Map<Integer, Float>> rawVertexBoneWeights = new HashMap<>(2 * mesh.mNumVertices());
		for (int b = 0; b < mesh.mNumBones(); b++) {
			AIBone bone = AIBone.create(mesh.mBones().get(b));
			for (int w = 0; w < bone.mNumWeights(); w++) {
				AIVertexWeight aiWeight = bone.mWeights().get(w);
				int vertex = aiWeight.mVertexId();
				Map<Integer, Float> vertexMapping = rawVertexBoneWeights.computeIfAbsent(vertex, k -> new HashMap<>());
				String boneName = bone.mName().dataString();
				int boneID = skeleton.firstBoneNamed(boneName).getIndex();
				vertexMapping.put(boneID, aiWeight.mWeight());
			}
			bone.close();
		}
		//normalize weights
		for (int v = 0; v < mesh.mNumVertices(); v++) {
			Map<Integer, Float> weights = rawVertexBoneWeights.get(v);
			if (weights == null) {
				weights = new HashMap<>();
			}
			while(weights.size() < weightsAmount) {
				//putting 0 at some non-existent index
				weights.put(-weights.size() - 1, 0f);
			}
			ArrayList<Map.Entry<Integer, Float>> list = new ArrayList<>(weights.entrySet());

			list.sort((Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2) -> o2.getValue().compareTo(o1.getValue()));

			while(list.size() > weightsAmount) {
				list.remove(list.size() - 1);
			}
			//adjusts weights
			float sum = list.stream().map(Map.Entry::getValue).reduce(0f, Float::sum);
			for (Map.Entry<Integer, Float> entry : list) {
				entry.setValue(entry.getValue() / sum);
			}

			vertexBoneWeights.put(v, list);
		}
		return vertexBoneWeights;
	}

	/**
	 * extract a skeleton from the scene structure and adjust the bones indices
	 *
	 * @param mesh
	 *            the mesh that contains bones
	 * @param sceneStructure
	 *            the total scene structure
	 * @return
	 */
	private Bone parseSkeleton(AIMesh mesh, Bone sceneStructure) {
		//todo: Try to merge this method with load Vertex weights
		//This is necessary to make sonar stop complaining about unclosed resources, because the assimp binding actually
		//destroys any object that is closed.
		Bone rootBone = null;
		for (int b = 0; b < mesh.mNumBones(); b++) {
			AIBone aibone = AIBone.create(mesh.mBones().get(b));
			String boneName = aibone.mName().dataString();
			Bone bone;
			if (rootBone == null) {
				bone = sceneStructure.firstBoneNamed(boneName);
				rootBone = bone;
			} else {
				bone = rootBone.firstBoneNamed(boneName);
			}
			bone.setInverseBindTransform(BoneTransformation.matrixFromAI(aibone.mOffsetMatrix()));
			bone.setIndex(b);
		}
		if (rootBone == null) {
			Log.error(TAG, "No Bones in AnimatedMesh!");
		}
		return rootBone;
	}

	/**
	 * return the scenes node structure as a Bone (where every object is
	 * (maybe falsely) represented as a bone)
	 *
	 * @param scene
	 * @return
	 */
	private Bone parseScene(AIScene scene) {
		return parseScene(scene.mRootNode());
	}

	/**
	 * return this nodes structure as a Bone (where every object is
	 * (maybe falsely) represented as a bone)
	 *
	 * @param node
	 * @return
	 */
	private Bone parseScene(AINode node) {
		Bone bone = new Bone(node.mName().dataString());
		bone.setDefaultBoneTransform(BoneTransformation.matrixFromAI(node.mTransformation()));
		for (int c = 0; c < node.mNumChildren(); c++) {
			AINode childNode = AINode.create(node.mChildren().get(c));
			bone.getChildren().add(parseScene(childNode));
		}
		return bone;
	}

	/**
	 * load all the animations in a scene for the given animatedMesh
	 * 
	 * @param animMesh
	 *            the mesh to load animations for
	 * @param scene
	 *            the aiScene to load from
	 */
	private void loadAnimations(AnimatedMesh animMesh, AIScene scene) {
		for (int a = 0; a < scene.mNumAnimations(); a++) {
			AIAnimation aianim = AIAnimation.create(scene.mAnimations().get(a));
			Animation anim = Animation.copySettingsFromAI(aianim);

			for (int channel = 0; channel < aianim.mNumChannels(); channel++) {
				AINodeAnim node = AINodeAnim.create(aianim.mChannels().get(channel));
				String boneName = node.mNodeName().dataString();

				Log.assertEqual(TAG, node.mNumPositionKeys(), node.mNumRotationKeys(), "unequal position and rotation key amount");
				Log.assertEqual(TAG, node.mNumScalingKeys(), node.mNumRotationKeys(), "unequal scaling and rotation key amount");

				for (int key = 0; key < node.mNumPositionKeys(); key++) {

					AIVectorKey pos = node.mPositionKeys().get(key);
					AIQuatKey rot = node.mRotationKeys().get(key);
					AIVectorKey scale = node.mScalingKeys().get(key);

					BoneTransformation transform = new BoneTransformation(
							new Vector3f(pos.mValue().x(), pos.mValue().y(), pos.mValue().z()),
							new Quaternionf(rot.mValue().x(), rot.mValue().y(), rot.mValue().z(), rot.mValue().w()),
							new Vector3f(scale.mValue().x(), scale.mValue().y(), scale.mValue().z())
					);
					KeyFrame keyFrame = new KeyFrame(
							pos.mTime(),
							new Pose(
									animMesh.getSkeleton(),
									animMesh.getTransform()).put(boneName, transform
							)
					);
					anim.mergeKeyFrame(keyFrame);
				}
				node.close();
			}

			animMesh.addAnimation(anim);
		}
	}

	/**
	 * adjust the local boundingboxes of the bones to contain passed vertex
	 */
	private void adjustBoneBoxes(
			Bone skeleton,
			Vector3f vertexPosition,
			int vertexID,
			Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights,
			Matrix4f sceneTransform
	) {
		List<Map.Entry<Integer, Float>> boneWeights = vertexBoneWeights.get(vertexID);

		for (Map.Entry<Integer, Float> boneWeight : boneWeights) {
			int targetIndex = boneWeight.getKey();
			if (targetIndex < 0 || boneWeight.getValue() < THRESHOLD) {
				continue;
			}
			Bone target = skeleton.firstBoneWithIndex(targetIndex);
			Matrix4f totalTrans = target.getInverseBindTransform().mul(sceneTransform, new Matrix4f());

			Vector4f inBoneSpace4 = totalTrans.transform(new Vector4f(vertexPosition, 1));
			Vector3f inBoneSpace = new Vector3f(inBoneSpace4.x, inBoneSpace4.y, inBoneSpace4.z);
			target.setBoundingBox(target.getBoundingBox().extendTo(inBoneSpace));
		}
	}

}
