package mbeb.opengldefault.scene;

import mbeb.opengldefault.rendering.VAORenderable;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

/**
 * Contains logic to create Renderables from Files
 */
public class ObjectLoader {

	/**
	 * load an object into a Renderable
	 * @param path the absolute File-Path to the object
	 * @param format which data to load
	 * @return a VAO-Renderable
	 */
	public IRenderable loadFromFile(String path, DataFragment[] format) {
		AIScene scene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate);
		for (int meshID = 0; meshID < scene.mNumMeshes(); meshID++) {
			IRenderable mesh = loadMesh(scene, meshID, format);
			//todo not return just the first mesh, rather combine meshes
			return mesh;
		}
		return null;
	}

	/**
	 * load a single mesh
	 * @param scene the AIScene to load from
	 * @param meshID which mesh to load
	 * @param format the format the data should be in
	 * @return 
	 */
	private VAORenderable loadMesh(AIScene scene, int meshID, DataFragment[] format) {
		long addr1 = scene.mMeshes().get(meshID);
		AIMesh mesh = AIMesh.create(addr1);

		int vertexCount = mesh.mNumVertices();

		float[] data = new float[vertexCount * DataFragment.getTotalSize(format)];
		int dataPointer = 0;
		int[] indices = new int[vertexCount];
		int indicesPointer = 0;

		for (int v = 0; v < vertexCount; v++) {
			for (DataFragment dataFormat : format) {
				dataFormat.addTo(mesh, v, data, dataPointer);
				dataPointer += dataFormat.size();
			}
			indices[indicesPointer] = indicesPointer;
			indicesPointer++;
		}

		return new VAORenderable(data, indices, DataFragment.mapFormat(format));
	}

}
