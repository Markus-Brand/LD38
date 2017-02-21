package mbeb.opengldefault.rendering.io;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.openglcontext.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;

import org.joml.*;
import org.lwjgl.assimp.*;

/**
 * Contains logic to create Renderables from Files
 */
public class ObjectLoader {

	public static final DataFragment[] PosNormUv = new DataFragment[] {DataFragment.POSITION, DataFragment.NORMAL, DataFragment.UV};
	public static final DataFragment[] PosNormUvAnim3 = new DataFragment[] {DataFragment.POSITION, DataFragment.NORMAL, DataFragment.UV, DataFragment.BONE_INDICES_3, DataFragment.BONE_WEIGHTS_3};

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
	 * @return a VAO-Renderable
	 */
	public IRenderable loadFromFileAnim(String path) {
		return loadFromFile(path, PosNormUvAnim3);
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
		AIScene scene = Assimp.aiImportFile(realPath, Assimp.aiProcess_Triangulate);

		Bone sceneStructure = parseScene(scene);

		for (int meshID = 0; meshID < scene.mNumMeshes(); meshID++) {
			IRenderable mesh = loadMesh(scene, meshID, format, sceneStructure);
			//todo not return just the first mesh, rather combine meshes
			return mesh;
		}
		System.err.println("NO OBJECT FOUND!");
		return null;
	}

	private String getExtractedPath(String rawPath) {
		File res = new File("res");
		if (!res.exists()) {
			res.mkdirs();
		}
		File export = new File(res, rawPath);
		if (!export.exists()) {
			try {
				Files.copy(OpenGLContext.class.getResourceAsStream("/mbeb/opengldefault/resources/" + rawPath), export.toPath());
			} catch(IOException ex) {
				Log.log(TAG, ex.getMessage() + " at extracting resource " + rawPath);
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

		final int vertexFloatCount = DataFragment.getTotalSize(format);
		float[] data = new float[vertexCount * vertexFloatCount];
		int dataPointer = 0;
		int[] indices = new int[vertexCount];
		int indicesPointer = 0;

		BoundingBox box = new BoundingBox.Empty();
		boolean isAnimated = DataFragment.needsBoneData(format);
		// vertex-id  3elements     bone-id  weight
		Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights = null;
		Bone skeleton = null;

		if (isAnimated) {
			skeleton = parseSkeleton(mesh, sceneStructure);
			vertexBoneWeights = loadVertexWeights(mesh, skeleton, 3);
		}

		for (int v = 0; v < vertexCount; v++) {
			AIVector3D position = mesh.mVertices().get(v);
			box = box.extendTo(new Vector3f(position.x(), position.y(), position.z()));
			for (DataFragment dataFormat : format) {
				dataFormat.addTo(mesh, v, data, dataPointer, vertexBoneWeights);
				dataPointer += dataFormat.size();
			}
			indices[indicesPointer] = indicesPointer;
			indicesPointer++;
		}
		VAORenderable vaomesh = new VAORenderable(data, indices, format, box);

		if (isAnimated) {
			Matrix4f sceneTransform = BoneTransformation.matrixFromAI(scene.mRootNode().mTransformation());
			AnimatedMesh animMesh = loadAnimatedMesh(mesh, vaomesh, skeleton, sceneTransform);

			loadAnimations(animMesh, scene);

			return new AnimatedRenderable(animMesh);
		} else {
			return vaomesh;
		}
	}

	/**
	 * load the per-vertex weights on the bones ()
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
			//System.err.println("mNumWeights = " + bone.mNumWeights());
			for (int w = 0; w < bone.mNumWeights(); w++) {
				AIVertexWeight aiWeight = bone.mWeights().get(w);
				int vertex = aiWeight.mVertexId();
				Map<Integer, Float> vertexMapping = rawVertexBoneWeights.get(vertex);
				if (vertexMapping == null) {
					vertexMapping = new HashMap<>();
					rawVertexBoneWeights.put(vertex, vertexMapping);
				}
				String boneName = bone.mName().dataString();
				int boneID = skeleton.firstBoneNamed(boneName).getIndex();
				vertexMapping.put(boneID, aiWeight.mWeight());
			}
		}
		//normalize weights
		for (int v = 0; v < mesh.mNumVertices(); v++) {
			Map<Integer, Float> weights = rawVertexBoneWeights.get(v);
			//System.err.println("weights = " + weights.size());
			while(weights.size() < weightsAmount) {
				//putting 0 at some non-existent index
				weights.put(-weights.size() - 1, 0f);
			}
			ArrayList<Map.Entry<Integer, Float>> list = new ArrayList<>(weights.entrySet());

			list.sort((Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2) -> -o1.getValue().compareTo(o2.getValue()));

			while(list.size() > weightsAmount) {
				list.remove(list.size() - 1);
			}
			//adjuts weights
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
	 *            the mesh that containts bones
	 * @param sceneStructure
	 *            the total scene structure
	 * @return
	 */
	private Bone parseSkeleton(AIMesh mesh, Bone sceneStructure) {
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
		return rootBone;
	}

	/**
	 * load all necessary data for an animated mesh
	 *
	 * @param mesh
	 *            the AI-mesh containing this data
	 * @param vaomesh
	 *            the VAORenderable (raw mesh data)
	 * @return a new AnimatedRenderable
	 */
	private AnimatedMesh loadAnimatedMesh(AIMesh mesh, VAORenderable vaomesh, Bone skeleton, Matrix4f sceneTransform) {
		//TODO: apply the sceneTransformation on the mesh to get rid of the hacky solution in Pose#updteUniform
		return new AnimatedMesh(vaomesh, skeleton);
	}

	/**
	 * return the scenes node structure as a Bone (where every object is
	 * (falsely) represented as a bone)
	 *
	 * @param scene
	 * @return
	 */
	private Bone parseScene(AIScene scene) {
		AINode rootNode = scene.mRootNode();
		Bone rootBone = new Bone(rootNode.mName().dataString(), -1);
		rootBone.setDefaultBoneTransform(BoneTransformation.matrixFromAI(rootNode.mTransformation()));
		parseBoneChildren(rootBone, rootNode);
		return rootBone;
	}

	/**
	 * sub-routine for #parseScene(AIScene) that recursively converts AINodes to
	 * Bones.
	 *
	 * @param bone
	 * @param node
	 */
	private void parseBoneChildren(Bone bone, AINode node) {
		for (int c = 0; c < node.mNumChildren(); c++) {
			AINode childNode = AINode.create(node.mChildren().get(c));
			Bone childBone = new Bone(childNode.mName().dataString(), -1);
			childBone.setDefaultBoneTransform(BoneTransformation.matrixFromAI(childNode.mTransformation()));
			parseBoneChildren(childBone, childNode);
			bone.getChildren().add(childBone);
		}
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

				assert node.mNumPositionKeys() == node.mNumRotationKeys();
				assert node.mNumScalingKeys() == node.mNumRotationKeys();

				for (int key = 0; key < node.mNumPositionKeys(); key++) {

					AIVectorKey pos = node.mPositionKeys().get(key);
					AIQuatKey rot = node.mRotationKeys().get(key);
					AIVectorKey scale = node.mScalingKeys().get(key);

					BoneTransformation transform =
							new BoneTransformation(new Vector3f(pos.mValue().x(), pos.mValue().y(), pos.mValue().z()), new Quaternionf(rot.mValue().x(), rot.mValue().y(), rot.mValue().z(), rot
									.mValue().w()), new Vector3f(scale.mValue().x(), scale.mValue().y(), scale.mValue().z()));
					KeyFrame keyFrame = new KeyFrame(pos.mTime(), new Pose(animMesh.getSkeleton()).put(boneName, transform));
					anim.mergeKeyFrame(keyFrame);
				}
			}

			animMesh.addAnimation(anim);
		}
	}

}
