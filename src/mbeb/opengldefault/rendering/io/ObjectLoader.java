package mbeb.opengldefault.rendering.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.Bone;

import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import org.lwjgl.assimp.AIBone;
import mbeb.opengldefault.scene.BoundingBox;
import org.joml.Vector3f;

import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
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
		boolean isAnimated = DataFragment.needsBoneData(format);//todo
		// vertex-id  3elements     bone-id  weight
		Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights = null;

		if (isAnimated) {
			System.err.println("isAnimated = " + isAnimated);
			Bone skeleton = parseSkeleton(mesh, sceneStructure);
			//calculate weights here
			vertexBoneWeights = new HashMap<>();
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
				while (weights.size() < 3) {
					//putting 0 at some non-existent index
					weights.put(-weights.size() - 1, 0f);
				}
				ArrayList<Map.Entry<Integer, Float>> list = new ArrayList<>(weights.entrySet());

				list.sort((Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2)
						-> -o1.getValue().compareTo(o2.getValue()));

				while (list.size() > 3) {
					list.remove(list.size() - 1);
				}
				vertexBoneWeights.put(v, list);
			}
			System.err.println("vertexBoneWeights = " + vertexBoneWeights.size());
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
			return loadAnimatedMesh(mesh, vaomesh, sceneStructure);
		} else {
			return vaomesh;
		}
	}
	
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
	private AnimatedMesh loadAnimatedMesh(AIMesh mesh, VAORenderable vaomesh, Bone sceneStructure) {
		//load bones
		Bone rootBone = null;

		for (int b = 0; b < mesh.mNumBones(); b++) {
			AIBone bone = AIBone.create(mesh.mBones().get(b));
			String boneName = bone.mName().dataString();
			System.err.println("boneName = " + boneName);

			if (rootBone == null) {
				rootBone = new Bone(boneName, b);
			} else {
				//todo bone hierachy?

			}
		}
		return new AnimatedMesh(vaomesh);
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

		parseBoneChildren(rootBone, rootNode);

		return rootBone;
	}

	private void parseBoneChildren(Bone bone, AINode node) {
		for (int c = 0; c < node.mNumChildren(); c++) {
			AINode childNode = AINode.create(node.mChildren().get(c));
			Bone childBone = new Bone(childNode.mName().dataString(), -1);
			parseBoneChildren(childBone, childNode);
			bone.getChildren().add(childBone);
		}
	}

}
