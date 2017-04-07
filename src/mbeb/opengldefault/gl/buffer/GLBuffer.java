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
		EBO(GL_ELEMENT_ARRAY_BUFFER);

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
	public void bufferData(IntBuffer buffer, int target) {
		glBufferData(type.getGLType(), buffer, target);
		GLErrors.checkForError(TAG, "glBufferData " + type);
	}
	public void bufferData(ByteBuffer buffer, int target) {
		glBufferData(type.getGLType(), buffer, target);
		GLErrors.checkForError(TAG, "glBufferData " + type);
	}
	public void bufferData(FloatBuffer buffer, int target) {
		glBufferData(type.getGLType(), buffer, target);
		GLErrors.checkForError(TAG, "glBufferData " + type);
	}
	public void bufferData(int[] buffer, int target) {
		glBufferData(type.getGLType(), buffer, target);
		GLErrors.checkForError(TAG, "glBufferData " + type);
	}
	public void bufferData(float [] buffer, int target) {
		glBufferData(type.getGLType(), buffer, target);
		GLErrors.checkForError(TAG, "glBufferData " + type);
	}
//</editor-fold>

}
