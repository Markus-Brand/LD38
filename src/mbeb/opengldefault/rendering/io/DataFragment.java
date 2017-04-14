package mbeb.opengldefault.rendering.io;

import java.util.*;

import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.logging.Log;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

/**
 * for parsing files: specify information wanted from the mesh
 */
public enum DataFragment {
	FLOAT {

		@Override
		public int size() {
			return 1;
		}

	},
	POSITION {

		@Override
		public int size() {
			return 3;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, GLBufferWriter writer) {
			AIVector3D vec = mesh.mVertices().get(v);
			writer.write(new Vector3f(vec.x(), vec.y(), vec.z()));
		}
	},
	POSITION2D {

		@Override
		public int size() {
			return 2;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, GLBufferWriter writer) {
			AIVector3D vec = mesh.mVertices().get(v);
			writer.write(new Vector2f(vec.x(), vec.y()));
		}
	},
	NORMAL {

		@Override
		public int size() {
			return 3;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, GLBufferWriter writer) {
			AIVector3D vec = mesh.mNormals().get(v);
			writer.write(new Vector3f(vec.x(), vec.y(), vec.z()));
		}
	},
	MOCK_NORMAL {

		@Override
		public int size() {
			return 3;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, GLBufferWriter writer) {
			//todo calculate normals here
			writer.write(new Vector3f(1, 0, 0));
		}
	},
	UV {

		@Override
		public int size() {
			return 2;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, GLBufferWriter writer) {
			AIVector3D vec = mesh.mTextureCoords(0).get(v);
			writer.write(new Vector2f(vec.x(), vec.y()));
		}
	},
	MOCK_UV {

		@Override
		public int size() {
			return 2;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, GLBufferWriter writer) {
			POSITION2D.addTo(mesh, v, writer);
		}
	},
	BONE_INDICES_3 {

		@Override
		public boolean needsBoneData() {
			return true;
		}

		@Override
		public int size() {
			return 3;
		}

		@Override
		public boolean isFloat() {
			return true;
		}

		@Override
		public void addTo(AIMesh mesh, int v, GLBufferWriter writer, Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights) {
			List<Map.Entry<Integer, Float>> weightsData = vertexBoneWeights.get(v);
			Log.assertTrue(TAG, weightsData.size() == 3, "unexpected amount of weights");
			for (Map.Entry<Integer, Float> e : weightsData) {
				writer.write((float)e.getKey());
			}
		}
	},
	BONE_WEIGHTS_3 {

		@Override
		public int size() {
			return 3;
		}

		@Override
		public void addTo(AIMesh mesh, int v, GLBufferWriter writer, Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights) {
			List<Map.Entry<Integer, Float>> weightsData = vertexBoneWeights.get(v);
			Log.assertTrue(TAG, weightsData.size() == 3, "unexpected amount of weights");
			for (Map.Entry<Integer, Float> e : weightsData) {
				writer.write(e.getValue());
			}
		}
	};

	private static final String TAG = "DataFragment";

	/**
	 * @return how many floats this DataFragment takes
	 */
	public abstract int size();

	/**
	 * add your data to the buffer
	 * 
	 * @param mesh
	 *            the mesh to read from
	 * @param v
	 *            the currently processed vertex index
	 * @param writer
	 */
	protected void addTo(AIMesh mesh, int v, GLBufferWriter writer) {
		Log.error(TAG, "addTo not implemented!");
	}

	/**
	 * add your data to the buffer, passing vertexWeights as well
	 * 
	 * @param mesh
	 * @param v
	 * @param writer
	 * @param vertexBoneWeights
	 */
	public void addTo(AIMesh mesh, int v, GLBufferWriter writer, Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights) {
		addTo(mesh, v, writer);
	}

	/**
	 * @return true if this fragment needs animation information
	 */
	public boolean needsBoneData() {
		return false;
	}

	public boolean isFloat() {
		return true;
	}

	////////////////////////////////////////////////////////////////////////

	/**
	 * @param dataFormat
	 * @return the combined size of all a format
	 */
	public static int getTotalSize(DataFragment[] dataFormat) {
		int sum = 0;
		for (DataFragment d : dataFormat) {
			sum += d.size();
		}
		return sum;
	}

	/**
	 * @param format
	 * @return the format array mapped to the sizes of the DataFragments
	 */
	public static int[] mapFormat(DataFragment[] format) {
		int[] res = new int[format.length];
		for (int i = 0; i < format.length; i++) {
			res[i] = format[i].size();
		}
		return res;
	}

	/**
	 * @param format
	 * @return true if this format needs animation information
	 */
	public static boolean needsBoneData(DataFragment[] format) {
		for (DataFragment frag : format) {
			if (frag.needsBoneData()) {
				return true;
			}
		}
		return false;
	}
}
