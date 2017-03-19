package mbeb.opengldefault.constants;

/**
 * A collection of miscellaneous constants.
 *
 * @author Potti
 */
public class Constants {

	/**
	 * The size of a float in bytes
	 */
	public static final int FLOAT_SIZE = 4;
	/**
	 * The size of a block in bytes
	 */
	public static final int BLOCK_SIZE = 16;

	//<editor-fold desc="VECTORS">
	/**
	 * The number of components of a vec2
	 */
	public static final int VEC2_COMPONENTS = 4;
	/**
	 * The size of a vec2 in bytes
	 */
	public static final int VEC2_SIZE = FLOAT_SIZE * VEC2_COMPONENTS;
	/**
	 * The number of components of a vec3
	 */
	public static final int VEC3_COMPONENTS = 3;
	/**
	 * The size of a vec3 in bytes
	 */
	public static final int VEC3_SIZE = FLOAT_SIZE * VEC3_COMPONENTS;
	/**
	 * The number of components of a vec3 (while filling a block)
	 */
	public static final int VEC3_BLOCK_COMPONENTS = 4;
	/**
	 * The size of a vec3 in bytes (while filling a block)
	 */
	public static final int VEC3_BLOCK_SIZE = FLOAT_SIZE * VEC3_BLOCK_COMPONENTS;
	/**
	 * The number of components of a vec4
	 */
	public static final int VEC4_COMPONENTS = 4;
	/**
	 * The size of a vec4 in bytes
	 */
	public static final int VEC4_SIZE = FLOAT_SIZE * VEC4_COMPONENTS;
	//</editor-fold>

	//<editor-fold desc="MATRICES">
	/**
	 * The number of components of a mat2
	 */
	public static final int MAT2_COMPONENTS = 4;
	/**
	 * The size of a mat2 in bytes
	 */
	public static final int MAT2_SIZE = MAT2_COMPONENTS * FLOAT_SIZE;
	/**
	 * The number of components of a mat3
	 */
	public static final int MAT3_COMPONENTS = 9;
	/**
	 * The size of a mat3 in bytes
	 */
	public static final int MAT3_SIZE = MAT3_COMPONENTS * FLOAT_SIZE;
	/**
	 * The number of components of a mat3 (while filling blocks)
	 */
	public static final int MAT3_BLOCK_COMPONENTS = 12;
	/**
	 * The size of a mat3 in bytes (while filling a block
	 */
	public static final int MAT3_BLOCK_SIZE = MAT3_BLOCK_COMPONENTS * FLOAT_SIZE;
	/**
	 * The number of components of a mat4
	 */
	public static final int MAT4_COMPONENTS = 4 * 4;
	/**
	 * The size of a mat4 in bytes
	 */
	public static final int MAT4_SIZE = FLOAT_SIZE * MAT4_COMPONENTS;
	//</editor-fold>
}
