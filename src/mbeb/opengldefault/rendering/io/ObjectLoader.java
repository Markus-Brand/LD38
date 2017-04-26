package mbeb.opengldefault.rendering.io;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.joml.*;
import org.lwjgl.assimp.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.gl.*;
import mbeb.opengldefault.gl.buffer.*;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;

/**
 * Contains logic to create Renderables from Files
 */
public class ObjectLoader {

	private static final DataFragment[] PosNormUv = new DataFragment[] {DataFragment.POSITION, DataFragment.NORMAL, DataFragment.TANGENT, DataFragment.UV};
	private static final DataFragment[] PosNormUvAnim3 =
			new DataFragment[] {DataFragment.POSITION, DataFragment.NORMAL, DataFragment.TANGENT, DataFragment.UV, DataFragment.BONE_INDICES_3, DataFragment.BONE_WEIGHTS_3};
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
	public IRenderable loadFromFile(final String path) {
		return loadFromFile(path, PosNormUv);
	}

	/**
	 * also load animation data (3 ids / 3 weights)
	 *
	 * @param path
	 *            the absolute File-Path to the object
	 * @return a Renderable
	 */
	public AnimatedMesh loadFromFileAnim(final String path) {
		final AnimatedMesh mesh = (AnimatedMesh) loadFromFile(path, PosNormUvAnim3);
		loadAnimationMetaData(mesh, path + ".meta.yaml");
		return mesh;
	}

	/**
	 * load the priorities of movements per bone
	 *
	 * @param mesh
	 *            the mesh to modify
	 * @param metaPath
	 *            path to the file containing this information
	 */
	private void loadAnimationMetaData(final AnimatedMesh mesh, final String metaPath) {
		final String extractedPath = getExtractedPath(metaPath);
		if (extractedPath == null) {
			return;
		}
		final YAMLParser.YAMLNode root = new YAMLParser(new File(extractedPath)).getRoot();
		System.out.println(root.getChildren().size());
		final YAMLParser.YAMLNode animations = root.getChildByName("animations");
		for (final YAMLParser.YAMLNode animNode : animations.getChildren().values()) {
			final Animation anim = mesh.getAnimationByName(animNode.getName());

			if (anim != null) {
				for (final YAMLParser.YAMLNode boneNode : animNode.getChildren().values()) {
					adjustBoneAnimationPriorities(anim, anim.getSkeleton(), boneNode);
				}
			}
		}

		final YAMLParser.YAMLNode sizeFactor = root.getChildByName("sizeFactor");
		if (sizeFactor != null) {
			mesh.setBoundingBoxSizeFactor(Float.valueOf(sizeFactor.getData()));
		}
	}

	/**
	 * apply one directive (one line of YAML) to the corresponding Animation
	 *
	 * @param anim
	 *            the Animation to alter
	 * @param bone
	 *            the root bone (anim.getSkeleton())
	 * @param boneNode
	 *            the directive to apply
	 */
	private void adjustBoneAnimationPriorities(final Animation anim, final Bone bone, final YAMLParser.YAMLNode boneNode) {
		if (bone.getName().toLowerCase().contains(boneNode.getName().toLowerCase())) {
			bone.foreach((final Bone ancestor) -> anim.setBonePriority(ancestor, Integer.valueOf(boneNode.getData())));
		} else {
			for (final Bone child : bone.getChildren()) {
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
	public IRenderable loadFromFile(final String path, final DataFragment[] format) {
		try {
			final String realPath = getExtractedPath(path);
			final AIScene scene = Assimp.aiImportFile(realPath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace);

			final Bone sceneStructure = parseScene(scene);

			if (scene.mNumMeshes() > 0) {
				return loadMesh(scene, 0, format, sceneStructure);
				//todo not return just the first mesh, rather combine meshes
			} else {
				Log.error(TAG, "No Mesh found in object");
				return null;
			}

		} catch(final Exception ex) {
			Log.error(TAG, "unable to load " + path, ex);
			return null;
		}
	}

	private String getExtractedPath(final String rawPath) {
		final File res = new File("res");
		if (!res.exists()) {
			res.mkdirs();
		}
		final File export = new File(res, rawPath);
		if (!export.exists()) {
			export.getParentFile().mkdirs();
			try {
				final InputStream inStream = GLContext.class.getResourceAsStream("/models/" + rawPath);
				if (inStream == null) {
					return null;
				}
				Files.copy(inStream, export.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch(final IOException ex) {
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
	private IRenderable loadMesh(final AIScene scene, final int meshID, final DataFragment[] format, final Bone sceneStructure) {
		final AIMesh mesh = AIMesh.create(scene.mMeshes().get(meshID));

		final int vertexCount = mesh.mNumVertices();

		final VAORenderable vaomesh = new VAORenderable(vertexCount, format);
		final GLBufferWriter dataWriter = vaomesh.dataWriter();

		BoundingBox box = new BoundingBox.Empty();
		final boolean isAnimated = DataFragment.needsBoneData(format);
		// vertex-id  3elements     bone-id  weight
		Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights = null;
		Bone skeleton = null;

		if (isAnimated) {
			skeleton = parseSkeleton(mesh, sceneStructure);
			vertexBoneWeights = loadVertexWeights(mesh, skeleton, 3);
		}
		final Matrix4f sceneTransform = BoneTransformation.matrixFromAI(scene.mRootNode().mTransformation());

		for (int v = 0; v < vertexCount; v++) {
			final AIVector3D aiposition = mesh.mVertices().get(v);
			final Vector3f position = new Vector3f(aiposition.x(), aiposition.y(), aiposition.z());
			box = box.extendTo(position);
			for (final DataFragment dataFormat : format) {
				dataFormat.addTo(mesh, v, dataWriter, vertexBoneWeights);
			}
		}
		mesh.close();

		dataWriter.flush(GLBufferWriter.WriteType.FULL_DATA);
		vaomesh.setAttribPointers();
		vaomesh.setBoundingBox(box);
		vaomesh.finishWriting();

		if (isAnimated) {
			final AnimatedMesh animMesh = new AnimatedMesh(vaomesh, skeleton);
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
	private Map<Integer, List<Map.Entry<Integer, Float>>> loadVertexWeights(final AIMesh mesh, final Bone skeleton, final int weightsAmount) {
		//calculate weights here
		final Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights = new HashMap<>();
		final Map<Integer, Map<Integer, Float>> rawVertexBoneWeights = new HashMap<>(2 * mesh.mNumVertices());
		for (int b = 0; b < mesh.mNumBones(); b++) {
			final AIBone bone = AIBone.create(mesh.mBones().get(b));
			for (int w = 0; w < bone.mNumWeights(); w++) {
				final AIVertexWeight aiWeight = bone.mWeights().get(w);
				final int vertex = aiWeight.mVertexId();
				final Map<Integer, Float> vertexMapping = rawVertexBoneWeights.computeIfAbsent(vertex, k -> new HashMap<>());
				final String boneName = bone.mName().dataString();
				final int boneID = skeleton.firstBoneNamed(boneName).getIndex();
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
			final ArrayList<Map.Entry<Integer, Float>> list = new ArrayList<>(weights.entrySet());

			list.sort((final Map.Entry<Integer, Float> o1, final Map.Entry<Integer, Float> o2) -> o2.getValue().compareTo(o1.getValue()));

			while(list.size() > weightsAmount) {
				list.remove(list.size() - 1);
			}
			//adjusts weights
			final float sum = list.stream().map(Map.Entry::getValue).reduce(0f, Float::sum);
			for (final Map.Entry<Integer, Float> entry : list) {
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
	private Bone parseSkeleton(final AIMesh mesh, final Bone sceneStructure) {
		//todo: Try to merge this method with load Vertex weights
		//This is necessary to make sonar stop complaining about unclosed resources, because the assimp binding actually
		//destroys any object that is closed.
		Bone rootBone = null;
		for (int b = 0; b < mesh.mNumBones(); b++) {
			final AIBone aibone = AIBone.create(mesh.mBones().get(b));
			final String boneName = aibone.mName().dataString();
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
	private Bone parseScene(final AIScene scene) {
		return parseScene(scene.mRootNode());
	}

	/**
	 * return this nodes structure as a Bone (where every object is
	 * (maybe falsely) represented as a bone)
	 *
	 * @param node
	 * @return
	 */
	private Bone parseScene(final AINode node) {
		final Bone bone = new Bone(node.mName().dataString());
		bone.setDefaultBoneTransform(BoneTransformation.matrixFromAI(node.mTransformation()));
		for (int c = 0; c < node.mNumChildren(); c++) {
			final AINode childNode = AINode.create(node.mChildren().get(c));
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
	private void loadAnimations(final AnimatedMesh animMesh, final AIScene scene) {
		for (int a = 0; a < scene.mNumAnimations(); a++) {
			final AIAnimation aianim = AIAnimation.create(scene.mAnimations().get(a));
			final Animation anim = Animation.copySettingsFromAI(aianim);

			for (int channel = 0; channel < aianim.mNumChannels(); channel++) {
				final AINodeAnim node = AINodeAnim.create(aianim.mChannels().get(channel));
				final String boneName = node.mNodeName().dataString();

				Log.assertEqual(TAG, node.mNumPositionKeys(), node.mNumRotationKeys(), "unequal position and rotation key amount");
				Log.assertEqual(TAG, node.mNumScalingKeys(), node.mNumRotationKeys(), "unequal scaling and rotation key amount");

				for (int key = 0; key < node.mNumPositionKeys(); key++) {

					final AIVectorKey pos = node.mPositionKeys().get(key);
					final AIQuatKey rot = node.mRotationKeys().get(key);
					final AIVectorKey scale = node.mScalingKeys().get(key);

					final BoneTransformation transform = new BoneTransformation(new Vector3f(pos.mValue().x(), pos.mValue().y(), pos.mValue().z()),
							new Quaternionf(rot.mValue().x(), rot.mValue().y(), rot.mValue().z(), rot.mValue().w()), new Vector3f(scale.mValue().x(), scale.mValue().y(), scale.mValue().z()));
					final KeyFrame keyFrame = new KeyFrame(pos.mTime(), new Pose(animMesh.getSkeleton(), animMesh.getTransform()).put(boneName, transform));

					anim.mergeKeyFrame(keyFrame);
				}
				node.close();
			}

			animMesh.addAnimation(anim);
		}
	}
}
