package mbeb.opengldefault.rendering.io;


import java.util.*;
import org.lwjgl.assimp.*;

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
		protected void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			AIVector3D vec = mesh.mVertices().get(v);
			data[dataPointer++] = vec.x();
			data[dataPointer++] = vec.y();
			data[dataPointer++] = vec.z();
		}
	},
	POSITION2D {

		@Override
		public int size() {
			return 2;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			AIVector3D vec = mesh.mVertices().get(v);
			data[dataPointer++] = vec.x();
			data[dataPointer++] = vec.y();
		}
	},
	NORMAL {

		@Override
		public int size() {
			return 3;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			AIVector3D vec = mesh.mNormals().get(v);
			data[dataPointer++] = vec.x();
			data[dataPointer++] = vec.y();
			data[dataPointer++] = vec.z();
		}
	},
	MOCK_NORMAL {

		@Override
		public int size() {
			return 3;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			//todo calculate normals here
			data[dataPointer++] = 1;
			data[dataPointer++] = 0;
			data[dataPointer++] = 0;
		}
	},
	UV {

		@Override
		public int size() {
			return 2;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			AIVector3D vec = mesh.mTextureCoords(0).get(v);
			data[dataPointer++] = vec.x();
			data[dataPointer++] = vec.y();
		}
	},
	MOCK_UV {

		@Override
		public int size() {
			return 2;
		}

		@Override
		protected void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
			POSITION2D.addTo(mesh, v, data, dataPointer);
		}
	}, BONE_INDICES_3 {
		
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
		public void addTo(AIMesh mesh, int v, float[] data, int dataPointer, Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights) {
			List<Map.Entry<Integer, Float>> weightsData = vertexBoneWeights.get(v);
			for (Map.Entry<Integer, Float> e: weightsData) {
				data[dataPointer++] = e.getKey();
			}
		}
	}, BONE_WEIGHTS_3 {

		@Override
		public int size() {
			return 3;
		}
		
		@Override
		public void addTo(AIMesh mesh, int v, float[] data, int dataPointer, Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights) {
			List<Map.Entry<Integer, Float>> weightsData = vertexBoneWeights.get(v);
			for (Map.Entry<Integer, Float> e: weightsData) {
				data[dataPointer++] = e.getValue();
			}
		}
	};

	/**
	 * @return how many floats this DataFragment takes
	 */
	public abstract int size();

	/**
	 * add your data to the buffer
	 * @param mesh the mesh to read from
	 * @param v the currently processed vertex index
	 * @param data the data array to store in
	 * @param dataPointer current array offset
	 */
	protected void addTo(AIMesh mesh, int v, float[] data, int dataPointer) {
		System.err.println("addTo not implemented!");
	}
	
	/**
	 * add your data to the buffer, passing vertexWeights aswell
	 * @param mesh
	 * @param v
	 * @param data
	 * @param dataPointer
	 * @param vertexBoneWeights 
	 */
	public void addTo(AIMesh mesh, int v, float[] data, int dataPointer, Map<Integer, List<Map.Entry<Integer, Float>>> vertexBoneWeights) {
		addTo(mesh, v, data, dataPointer);
	}

	/**
	 * 
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
	 * 
	 * @param format
	 * @return true if this format needs animation information
	 */
	public static boolean needsBoneData(DataFragment[] format) {
		for (DataFragment frag: format) {
			if (frag.needsBoneData()) {
				return true;
			}
		}
		return false;
	}
}
