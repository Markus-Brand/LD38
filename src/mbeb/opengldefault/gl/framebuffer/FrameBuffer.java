package mbeb.opengldefault.gl.framebuffer;

import static org.lwjgl.opengl.GL30.*;

import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.ContextBindings;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a framebuffer object in OpenGL.
 */
public class FrameBuffer extends GLObject {
	private static final String TAG = "FrameBuffer";

	/**
	 * The attachment point of a texture on a FrameBuffer.
	 */
	public enum Attachment {
		COLOR0(GL_COLOR_ATTACHMENT0), COLOR1(GL_COLOR_ATTACHMENT1), COLOR2(GL_COLOR_ATTACHMENT2), COLOR3(GL_COLOR_ATTACHMENT3), COLOR4(GL_COLOR_ATTACHMENT4), COLOR5(GL_COLOR_ATTACHMENT5),
		COLOR6(GL_COLOR_ATTACHMENT6), COLOR7(GL_COLOR_ATTACHMENT7), DEPTH(GL_DEPTH_ATTACHMENT), STENCIL(GL_STENCIL_ATTACHMENT), DEPTH_STENCIL(GL_DEPTH_STENCIL_ATTACHMENT);

		private int glEnum;

		Attachment(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this attachment point
		 */
		public int getGLEnum() {
			return glEnum;

		}
	}

	/**
	 * The map of currently attached textures.
	 */
	private Map<Attachment, Texture> attachments = new EnumMap<>(Attachment.class);

	/**
	 * Creates a new frame buffer object.
	 */
	public FrameBuffer() {
		super();
	}

	@Override
	protected Integer glGenerate() {
		int handle = glGenFramebuffers();
		return !GLErrors.checkForError(TAG, "glGenFramebuffers") ? handle : null;
	}

	@Override
	protected boolean glBind() {
		ContextBindings.bind(this);
		glBindFramebuffer(GL_FRAMEBUFFER, this.getHandle());
		boolean success = !GLErrors.checkForError(TAG, "glBindFramebuffer");
		if (!success) {
			ContextBindings.unbindFBO();
		}
		return success;
	}

	@Override
	protected boolean isBoundToContext() {
		return ContextBindings.isBound(this);
	}

	@Override
	protected boolean glUnbind() {
		ContextBindings.unbindFBO();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		return !GLErrors.checkForError(TAG, "glBindFramebuffer");
	}

	@Override
	protected boolean glDelete() {
		glDeleteFramebuffers(this.getHandle());
		return !GLErrors.checkForError(TAG, "glDeleteFramebuffers");
	}
}
