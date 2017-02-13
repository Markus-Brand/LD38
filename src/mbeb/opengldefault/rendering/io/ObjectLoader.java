package mbeb.opengldefault.rendering.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimatedRenderable;
import mbeb.opengldefault.animation.Animation;
import mbeb.opengldefault.animation.Bone;
import mbeb.opengldefault.animation.KeyFrame;
import mbeb.opengldefault.animation.Pose;

import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import org.lwjgl.assimp.AIBone;
import mbeb.opengldefault.scene.BoundingBox;
import mbeb.opengldefault.animation.BoneTransformation;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIAnimation;

import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIQuatKey;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVectorKey;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;

/**
 * Contains logic to create Renderables from Files
 */
public class ObjectLoader {

	public static final DataFragment[] PosNormUv = new DataFragment[]{DataFragment.POSITION, DataFragment.NORMAL, DataFragment.UV};
	public static final DataFragment[] PosNormUvAnim3 = new DataFragment[]{DataFragment.POSITION, DataFragment.NORMAL, DataFragment.UV, DataFragment.BONE_INDICES_3, DataFragment.BONE_WEIGHTS_3};

	private static final String TAG = "ObjectLoader";

	/**
	 * load an object into a Renderable. Assumes, that the file contains 3
	 * Position, 3 Normal and 2 UV Fragments
	 *
	 * @param path the absolute File-Path to the object
	 * @return a VAO-Renderable
	 */
	public IRenderable loadFromFile(String path) {
		return loadFromFile(path, PosNormUv);
	}

	/**
	 * also load animation data (3 ids / 3 weights)
	 *
	 * @param path the absolute File-Path to the object
	 * @return a VAO-Renderable
	 */
	public IRenderable loadFromFileAnim(String path) {
		return loadFromFile(path, PosNormUvAnim3);
	}

	/**
	 * load an object into a Renderable
	 *
	 * @param path the absolute File-Path to the object
	 * @param format which data to load
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
			} catch (IOException ex) {
				Log.log(TAG, ex.getMessage() + " at extracting resource " + rawPath);
			}
		}
		return "res/" + rawPath;
	}

	/**
	 * load a single mesh
	 *
	 * @param scene the AIScene to load from
	 * @param meshID which mesh to load
	 * @param format the format the data should be in
	 * @return
	 */
	private IRenderable loadMesh(AIScene scene, int meshID, DataFragment[] format, Bone sceneStructure) {
		long addr1 = scene.mMeshes().get(meshID);
		AIMesh mesh = AIMesh.create(addr1);

		int vertexCount = mesh.mNumVertices();

		float[] data = new float[vertexCount * DataFragment.getTotalSize(format)];
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
			AnimatedMesh animMesh = loadAnimatedMesh(mesh, vaomesh, skeleton);

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
	 * @param weightsAmount how many bones per vertex
	 * @return
	 */
	private Map<Integer, List<Map.Entry<Integer, Float>>> loadVertexWeights(AIMesh mesh, Bone skeleton, int weightsAmount) {
		//calculate weights here
		Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights = new HashMap<>();
		Map<Integer, Map<Integer, Float>> rawVertexBoneWeights = new HashMap<>(2 * mesh.mNumVertices());
		for (int b = 0; b < mesh.mNumBones(); b++) {
			AIBone bone = AIBone.create(mesh.mBones().get(b));
			System.err.println("mNumWeights = " + bone.mNumWeights());
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
		System.err.println("rawVertexBoneWeights = " + rawVertexBoneWeights.size());
		//normalize weights
		for (int v = 0; v < mesh.mNumVertices(); v++) {
			Map<Integer, Float> weights = rawVertexBoneWeights.get(v);
			//System.err.println("weights = " + weights.size());
			while (weights.size() < weightsAmount) {
				//putting 0 at some non-existent index
				weights.put(-weights.size() - 1, 0f);
			}
			ArrayList<Map.Entry<Integer, Float>> list = new ArrayList<>(weights.entrySet());

			list.sort((Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2)
					-> -o1.getValue().compareTo(o2.getValue()));

			while (list.size() > weightsAmount) {
				list.remove(list.size() - 1);
			}
			vertexBoneWeights.put(v, list);
		}
		System.err.println("vertexBoneWeights = " + vertexBoneWeights.size());
		return vertexBoneWeights;
	}

	/**
	 * extract a skeleton from the scene structure and adjust the bones indices
	 *
	 * @param mesh the mesh that containts bones
	 * @param sceneStructure the total scene structure
	 * @return
	 */
	private Bone parseSkeleton(AIMesh mesh, Bone sceneStructure) {
		Bone rootBone = null;
		for (int b = 0; b < mesh.mNumBones(); b++) {
			AIBone aibone = AIBone.create(mesh.mBones().get(b));
			if (rootBone == null) {
				rootBone = sceneStructure.firstBoneNamed(aibone.mName().dataString());
				rootBone.setIndex(b);
			} else {
				rootBone.firstBoneNamed(aibone.mName().dataString()).setIndex(b);
			}
		}
		return rootBone;
	}

	/**
	 * load all necessary data for an animated mesh
	 *
	 * @param mesh the AI-mesh containing this data
	 * @param vaomesh the VAORenderable (raw mesh data)
	 * @return a new AnimatedRenderable
	 */
	private AnimatedMesh loadAnimatedMesh(AIMesh mesh, VAORenderable vaomesh, Bone skeleton) {
		skeleton.updateInverseBindTransform(new Matrix4f());
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
		rootBone.setLocalBindTransform(BoneTransformation.matFromAI(rootNode.mTransformation()));

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
			childBone.setLocalBindTransform(BoneTransformation.matFromAI(childNode.mTransformation()));
			parseBoneChildren(childBone, childNode);
			bone.getChildren().add(childBone);
		}
	}

	/**
	 * load all the animations in a scene for the given animatedMesh
	 * @param animMesh the mesh to load animations for
	 * @param scene the aiScene to load from
	 */
	private void loadAnimations(AnimatedMesh animMesh, AIScene scene) {
		for (int a = 0; a < scene.mNumAnimations(); a++) {
			AIAnimation aianim = AIAnimation.create(scene.mAnimations().get(a));
			Animation anim = Animation.copySettingsFromAI(aianim);
			
			System.out.println(aianim.mName().dataString());
			System.out.println(aianim.mDuration());
			System.out.println(aianim.mTicksPerSecond());
			System.out.println(aianim.mNumChannels());
			System.out.println(aianim.mNumMeshChannels());

			for (int channel = 0; channel < aianim.mNumChannels(); channel++) {
				AINodeAnim node = AINodeAnim.create(aianim.mChannels().get(channel));
				String boneName = node.mNodeName().dataString();
				
				assert node.mNumPositionKeys() == node.mNumRotationKeys();
				assert node.mNumScalingKeys() == node.mNumRotationKeys();
				
				for (int key = 0; key < node.mNumPositionKeys(); key++) {
					
					AIVectorKey pos = node.mPositionKeys().get(key);
					AIQuatKey rot = node.mRotationKeys().get(key);
					AIVectorKey scale = node.mScalingKeys().get(key);
					
					BoneTransformation transform = new BoneTransformation(
							new Vector3f(pos.mValue().x(), pos.mValue().y(), pos.mValue().z()),
							new Quaternionf(rot.mValue().x(), rot.mValue().y(), rot.mValue().z(), rot.mValue().w()),
							new Vector3f(scale.mValue().x(), scale.mValue().y(), scale.mValue().z()));
					KeyFrame keyFrame = new KeyFrame(pos.mTime(), new Pose(animMesh.getSkeleton()).put(boneName, transform));
					anim.mergeKeyFrame(keyFrame);
				}
			}
			System.out.println(anim.getKeyFrames().get(2));
			
			
			
			animMesh.addAnimation(anim);
		}

	}

}
