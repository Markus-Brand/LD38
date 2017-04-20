package mbeb.opengldefault.gl.buffer;

import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

import mbeb.opengldefault.logging.GLErrors;

/**
 * A Uniform Buffer Object
 */
public class UniformBuffer extends GLBuffer {

	private static final String TAG = "UniformBuffer";

	/** the index this UBO is meant to bind to */
	private final int baseIndex;
	/** the name of the UBO */
	private final String baseName;
	/** how big the ubo is */
	private int bufferSize;

	/**
	 * declare a new UniformBuffer
	 * 
	 * @param baseIndex
	 *            the index of this buffer
	 * @param baseName
	 *            the name of this buffer
	 * @param bufferSize
	 */
	public UniformBuffer(int baseIndex, String baseName, int bufferSize) {
		super(Type.UBO);
		this.baseIndex = baseIndex;
		this.baseName = baseName;
		this.bufferSize = bufferSize;
	}

	/**
	 * bind the already specified base of the UniformBuffer as well
	 */
	public void bindBufferBase() {
		glBindBufferBase(getType().getGLEnum(), baseIndex, ensureHandle());
		GLErrors.checkForError(TAG, "glBindBufferBase");
	}

	/**
	 * make a shader use this UniformBuffer
	 * 
	 * @param shaderProgramHandle
	 *            the shaderProgram to attach this object
	 */
	public void attachToShader(final int shaderProgramHandle) {
		final int uniformBlockIndex = glGetUniformBlockIndex(shaderProgramHandle, baseName);
		GLErrors.checkForError(TAG, "glGetUniformBlockIndex");

		glUniformBlockBinding(shaderProgramHandle, uniformBlockIndex, baseIndex);
		GLErrors.checkForError(TAG, "glUniformBlockBinding");
	}

	/**
	 * @return the number of bytes this UBO takes
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * set a new number of bytes for this UBO (e.g. after re-scaling arrays)
	 * 
	 * @param bufferSize
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		bufferData(bufferSize, Usage.STATIC_DRAW);
	}

	/**
	 * @return the base index of this uniform buffer
	 */
	public int getBaseIndex() {
		return baseIndex;
	}

	/**
	 * @return the base name of this uniform buffer
	 */
	public String getBaseName() {
		return baseName;
	}

	/**
	 * creates a writer based on the BufferSize of the UBO
	 * 
	 * @return a GLBufferWriter to write with
	 * @see #getBufferSize()
	 * @see GLBuffer#writer(int)
	 */
	public GLBufferWriter writer() {
		return writer(getBufferSize());
	}

	/**
	 * creates a writer based on the BufferSize of this UBO
	 * 
	 * @param offset
	 *            the offset in the buffer / where to start writing
	 * @return a GLBufferWriter to write with
	 * @see #getBufferSize()
	 * @see GLBuffer#writer(int, long)
	 */
	public GLBufferWriter writer(long offset) {
		return writer(getBufferSize() - (int) offset, offset);
	}
}
