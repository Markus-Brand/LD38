package mbeb.opengldefault.rendering.io;

import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

/**
 * for parsing files: specify information wanted from the mesh
 */
public enum DataFragment {
	
	POSITION {

		@Override
		public int size() {
			return 3;
		}

		@Override
		public void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			AIVector3D vec = mesh.mVertices().get(v);
			data[dataPointer++] = vec.x();
			data[dataPointer++] = vec.y();
			data[dataPointer++] = vec.z();
		}
	}, NORMAL {

		@Override
		public int size() {
			return 3;
		}

		@Override
		public void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			AIVector3D vec = mesh.mNormals().get(v);
			data[dataPointer++] = vec.x();
			data[dataPointer++] = vec.y();
			data[dataPointer++] = vec.z();
		}
	}, MOCK_NORMAL {

		@Override
		public int size() {
			return 3;
		}

		@Override
		public void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			//todo calculate normals here
			data[dataPointer++] = 1;
			data[dataPointer++] = 0;
			data[dataPointer++] = 0;
		}
	}, UV {

		@Override
		public int size() {
			return 2;
		}

		@Override
		public void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			AIVector3D vec = mesh.mTextureCoords(0).get(v);
			data[dataPointer++] = vec.x();
			data[dataPointer++] = vec.y();
		}
	}, MOCK_UV {

		@Override
		public int size() {
			return 2;
		}

		@Override
		public void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			data[dataPointer++] = 0;
			data[dataPointer++] = 0;
		}
	};

	/**
	 * @return how many floats this DataFragment takes
	 */
	public abstract int size();

	/**
	 * add your data to the buffer
	 * @param mesh the mesh to read from
	 * @param v the currently processed vertex
	 * @param data the data array to store in
	 * @param dataPointer current array offset
	 */
	public abstract void addTo(AIMesh mesh, int v, float[] data, int dataPointer);
	
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
}
