package mbeb.opengldefault.rendering.io;

import java.io.*;
import java.nio.file.*;

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

	private static final String TAG = "ObjectLoader";

	/**
	 * load an object into a Renderable.
	 * Assumes, that the file contains 3 Position, 3 Normal and 2 UV Fragments
	 *
	 * @param path
	 *            the absolute File-Path to the object
	 * @return a VAO-Renderable
	 */
	public IRenderable loadFromFile(String path) {

		return loadFromFile(path, new DataFragment[] {DataFragment.POSITION, DataFragment.NORMAL, DataFragment.UV});
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

		for (int meshID = 0; meshID < scene.mNumMeshes(); meshID++) {
			IRenderable mesh = loadMesh(scene, meshID, format);
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
	private VAORenderable loadMesh(AIScene scene, int meshID, DataFragment[] format) {
		long addr1 = scene.mMeshes().get(meshID);
		AIMesh mesh = AIMesh.create(addr1);

		int vertexCount = mesh.mNumVertices();

		float[] data = new float[vertexCount * DataFragment.getTotalSize(format)];
		int dataPointer = 0;
		int[] indices = new int[vertexCount];
		int indicesPointer = 0;

		BoundingBox box = new BoundingBox.Empty();

		for (int v = 0; v < vertexCount; v++) {
			AIVector3D position = mesh.mVertices().get(v);
			box = box.extendTo(new Vector3f(position.x(), position.y(), position.z()));
			for (DataFragment dataFormat : format) {
				dataFormat.addTo(mesh, v, data, dataPointer);
				dataPointer += dataFormat.size();
			}
			indices[indicesPointer] = indicesPointer;
			indicesPointer++;
		}

		return new VAORenderable(data, indices, DataFragment.mapFormat(format), box);
	}

}
