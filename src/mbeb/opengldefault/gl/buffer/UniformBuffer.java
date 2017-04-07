package mbeb.opengldefault.gl.buffer;

import mbeb.opengldefault.logging.GLErrors;

import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * A Uniform Buffer Object
 */
public class UniformBuffer extends GLBuffer {

	private static final String TAG = "UniformBuffer";

	private final int baseIndex;
	private final String baseName;

	/**
	 * declare a new UniformBuffer
	 * @param baseIndex the index of this buffer
	 * @param baseName the name of this buffer
	 */
	public UniformBuffer(int baseIndex, String baseName) {
		super(Type.UBO);
		this.baseIndex = baseIndex;
		this.baseName = baseName;
	}

	/**
	 * bind the already specified base of the UniformBuffer as well
	 */
	public void bindBufferBase() {
		glBindBufferBase(getType().getGLType(), baseIndex, ensureHandle());
		GLErrors.checkForError(TAG, "glBindBufferBase");
	}

	/**
	 * make a shader use this UniformBuffer
	 * @param shaderProgramHandle the shaderProgram to attach this object
	 */
	public void attachToShader(final int shaderProgramHandle) {
		final int uniformBlockIndex = glGetUniformBlockIndex(shaderProgramHandle, baseName);
		GLErrors.checkForError(TAG, "glGetUniformBlockIndex");

		glUniformBlockBinding(shaderProgramHandle, uniformBlockIndex, baseIndex);
		GLErrors.checkForError(TAG, "glUniformBlockBinding");
	}
}
