package mbeb.opengldefault.gl.buffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.openglcontext.ContextBindings;

/**
 * Describing all the Buffers that exist in OpenGL
 */
public abstract class GLBuffer extends GLObject {

	private static final String TAG = "GLBuffer";

	/**
	 * the possible types of a gl-buffer
	 */
	public enum Type {
		/**
		 * the currently bound {@link VertexBuffer}
		 */
		VBO(GL_ARRAY_BUFFER),
		/**
		 * the currently bound {@link ElementBuffer}
		 */
		EBO(GL_ELEMENT_ARRAY_BUFFER),
		/**
		 * the currently bound {@link UniformBuffer}
		 */
		UBO(GL_UNIFORM_BUFFER);

		private final int glEnum;

		Type(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this buffer type
		 */
		protected int getGLEnum() {
			return glEnum;
		}
	}

	/**
	 * The usage hint to OpenGL for the data store of this buffer.
	 * This tells the implementation how the data is going to be used.
	 */
	public enum Usage {
		/**
		 * The data store contents will be modified once by the application and used many times for drawing.
		 */
		STATIC_DRAW(GL_STATIC_DRAW),
		/**
		 * The data store contents will be repeatedly modified by the application and used many times for drawing.
		 */
		DYNAMIC_DRAW(GL_DYNAMIC_DRAW),
		/**
		 * The data store contents will be modified once by the application and used a few times for drawing.
		 */
		STREAM_DRAW(GL_STREAM_DRAW);

		private int glEnum;

		Usage(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this format
		 */
		protected int getGLEnum() {
			return glEnum;
		}
	}

	/** the type of this buffer */
	private Type type;

	/**
	 * create a new Buffer with a specified Type
	 * 
	 * @param type
	 */
	GLBuffer(Type type) {
		this.type = Log.assertNotNull(TAG, type);
	}

	/**
	 * @return the type of this Buffer
	 */
	public Type getType() {
		return type;
	}

	//<editor-fold desc="GLObject">
	@Override
	protected Integer glGenerate() {
		int generated = glGenBuffers();
		return GLErrors.checkForError(TAG, "glGenBuffers " + type) ? null : generated;
	}

	@Override
	protected boolean glDelete() {
		glDeleteBuffers(getHandle());
		return !GLErrors.checkForError(TAG, "glDeleteBuffers " + type);
	}

	@Override
	protected boolean glBind() {
		glBindBuffer(type.getGLEnum(), this.ensureHandle());
		boolean success = !GLErrors.checkForError(TAG, "glBindBuffer " + type);
		if (success) {
			ContextBindings.bind(this);
		}
		return success;
	}

	@Override
	protected boolean isBoundToContext() {
		return ContextBindings.isBound(this);
	}

	@Override
	protected boolean glUnbind() {
		glBindBuffer(type.getGLEnum(), 0);
		boolean success = !GLErrors.checkForError(TAG, "glUnBindBuffer " + type);
		if (success) {
			ContextBindings.unbind(this);
		}
		return success;
	}
	//</editor-fold>

	//<editor-fold desc="bufferData">
	public void bufferData(long size, Usage usage) {
		glBufferData(type.getGLEnum(), size, usage.getGLEnum());
		checkBufferError();
	}

	public void bufferData(IntBuffer buffer, Usage usage) {
		glBufferData(type.getGLEnum(), prepareBuffer(buffer), usage.getGLEnum());
		checkBufferError();
	}

	public void bufferData(ByteBuffer buffer, Usage usage) {
		glBufferData(type.getGLEnum(), prepareBuffer(buffer), usage.getGLEnum());
		checkBufferError();
	}

	public void bufferData(FloatBuffer buffer, Usage usage) {
		glBufferData(type.getGLEnum(), prepareBuffer(buffer), usage.getGLEnum());
		checkBufferError();
	}

	public void bufferData(int[] buffer, Usage usage) {
		glBufferData(type.getGLEnum(), buffer, usage.getGLEnum());
		checkBufferError();
	}

	public void bufferData(float[] buffer, Usage usage) {
		glBufferData(type.getGLEnum(), buffer, usage.getGLEnum());
		checkBufferError();
	}

	/**
	 * prepare a java.nio.Buffer to be sent on a GLBuffer
	 * 
	 * @param buffer
	 *            the Buffer object to prepare
	 * @param <B>
	 *            the type of the buffer
	 * @return the prepared buffer
	 */
	private <B extends Buffer> B prepareBuffer(B buffer) {
		buffer.rewind();
		return buffer;
	}

	/**
	 * check for an error after calling glBufferData
	 */
	private void checkBufferError() {
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
	//</editor-fold>

	//<editor-fold desc="bufferSubData">
	public void bufferSubData(long offset, IntBuffer buffer) {
		glBufferSubData(type.getGLEnum(), offset, prepareBuffer(buffer));
		checkBufferError();
	}

	public void bufferSubData(long offset, ByteBuffer buffer) {
		glBufferSubData(type.getGLEnum(), offset, prepareBuffer(buffer));
		checkBufferError();
	}

	public void bufferSubData(long offset, FloatBuffer buffer) {
		glBufferSubData(type.getGLEnum(), offset, prepareBuffer(buffer));
		checkBufferError();
	}

	public void bufferSubData(long offset, int[] buffer) {
		glBufferSubData(type.getGLEnum(), offset, buffer);
		checkBufferError();
	}

	public void bufferSubData(long offset, float[] buffer) {
		glBufferSubData(type.getGLEnum(), offset, buffer);
		checkBufferError();
	}
	//</editor-fold>

	/**
	 * create a GLBufferWriter that starts writing at the beginning of the buffer
	 * 
	 * @param capacity
	 *            the amount of primitives you intent to write
	 * @return a GLBufferWriter
	 * @see #writer(int, long)
	 */
	public GLBufferWriter writer(int capacity) {
		return writer(capacity, 0);
	}

	/**
	 * create a new GLBufferWriter to cache multiple write calls and to perform one single glBufferSubData in the end
	 * 
	 * @param capacity
	 *            the amount of primitives you intent to write
	 * @param offset
	 *            the offset in the buffer / where to start writing
	 * @return a GLBufferWriter to write with
	 */
	public GLBufferWriter writer(int capacity, long offset) {
		return new GLBufferWriter(this, offset, capacity);
	}

}
