package mbeb.opengldefault.gl.buffer;

import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.openglcontext.ContextBindings;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

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

		private final int glType;

		Type(int glType) {
			this.glType = glType;
		}

		public int getGLType() {
			return glType;
		}
	}

	/** the type of this buffer */
	private Type type;

	/**
	 * create a new Buffer with a specified Type
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
		glBindBuffer(type.getGLType(), this.ensureHandle());
		if (GLErrors.checkForError(TAG, "glBindBuffer " + type)) {
			return false;
		} else {
			ContextBindings.bind(this);
			return true;
		}
	}

	@Override
	protected boolean isBoundToContext() {
		return ContextBindings.isBound(this);
	}

	@Override
	protected boolean glUnbind() {
		glBindBuffer(type.getGLType(), 0);
		if (GLErrors.checkForError(TAG, "glUnBindBuffer " + type)) {
			return false;
		} else {
			ContextBindings.unbind(this);
			return true;
		}
	}
//</editor-fold>

//<editor-fold desc="bufferData">
	public void bufferData(long size, int usage) {
		glBufferData(type.getGLType(), size, usage);
		GLErrors.checkForError(TAG, "glBufferData " + type);
	}

	public void bufferData(IntBuffer buffer, int usage) {
		buffer.rewind();
		glBufferData(type.getGLType(), buffer, usage);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
	public void bufferData(ByteBuffer buffer, int usage) {
		buffer.rewind();
		glBufferData(type.getGLType(), buffer, usage);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
	public void bufferData(FloatBuffer buffer, int usage) {
		buffer.rewind();
		glBufferData(type.getGLType(), buffer, usage);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
	public void bufferData(int[] buffer, int usage) {
		glBufferData(type.getGLType(), buffer, usage);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
	public void bufferData(float [] buffer, int usage) {
		glBufferData(type.getGLType(), buffer, usage);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
//</editor-fold>

//<editor-fold desc="bufferSubData">
	public void bufferSubData(long offset, IntBuffer buffer) {
		buffer.rewind();
		glBufferSubData(type.getGLType(),offset,  buffer);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
	public void bufferSubData(long offset, ByteBuffer buffer) {
		buffer.rewind();
		glBufferSubData(type.getGLType(), offset,  buffer);
		GLErrors.checkForError(TAG, "glBufferData " + type + " " + offset + " " + buffer, true);
	}
	public void bufferSubData(long offset, FloatBuffer buffer) {
		buffer.rewind();
		glBufferSubData(type.getGLType(),offset,  buffer);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
	public void bufferSubData(long offset, int[] buffer) {
		glBufferSubData(type.getGLType(),offset,  buffer);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
	public void bufferSubData(long offset, float [] buffer) {
		glBufferSubData(type.getGLType(),offset,  buffer);
		GLErrors.checkForError(TAG, "glBufferData " + type, true);
	}
//</editor-fold>
	
	/**
	 * create a GLBufferWriter that starts writing at the beginning of the buffer
	 * @param capacity the amount of primitives you intent to write
	 * @return a GLBufferWriter
	 * @see #writer(int, long)
	 */
	public GLBufferWriter writer(int capacity) {
		return writer(capacity, 0);
	}
	
	/**
	 * create a new GLBufferWriter to cache multiple write calls and to perform one single glBufferSubData in the end
	 * @param capacity the amount of primitives you intent to write
	 * @param offset the offset in the buffer / where to start writing
	 * @return a GLBufferWriter to write with
	 */
	public GLBufferWriter writer(int capacity, long offset) {
		return new GLBufferWriter(this, offset, capacity);
	}

}
