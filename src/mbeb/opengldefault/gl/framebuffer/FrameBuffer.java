package mbeb.opengldefault.gl.framebuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

import java.util.EnumMap;
import java.util.Map;

import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.gl.texture.CubeMap;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.ContextBindings;

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

	/**
	 * Convenience method for glFramebufferTexture2D.
	 * 
	 * @param attachment
	 *            the attachment target
	 * @param texture
	 *            the texture to attach
	 * @param level
	 *            the level of the texture to attach
	 * @return whether the operation succeeded
	 */
	protected boolean attachTexture2D(Attachment attachment, Texture2D texture, int level) {
		return this.whileBound((FrameBuffer buffer) -> {
			glFramebufferTexture2D(GL_FRAMEBUFFER, attachment.getGLEnum(), texture.getType().getGLEnum(), texture.getHandle(), level);
			return !GLErrors.checkForError(TAG, "glFramebufferTexture2D");
		});
	}

	/**
	 * Convenience method for glFramebufferTexture2D with cubemaps.
	 *
	 * @param attachment
	 *            the attachment target
	 * @param texture
	 *            the texture to attach
	 * @param face
	 *            the face of the cubemap to attach
	 * @param level
	 *            the level of the texture to attach
	 * @return whether the operation succeeded
	 */
	protected boolean attachCubeMapFace(Attachment attachment, CubeMap texture, CubeMap.Face face, int level) {
		return this.whileBound((FrameBuffer buffer) -> {
			glFramebufferTexture2D(GL_FRAMEBUFFER, attachment.getGLEnum(), face.getGlEnum(), texture.getHandle(), level);
			return !GLErrors.checkForError(TAG, "glFramebufferTexture2D");
		});
	}

	/**
	 * Convenience method for glFramebufferTextureLayer.
	 *
	 * @param attachment
	 *            the attachment target
	 * @param texture
	 *            the texture to attach
	 * @param level
	 *            the level of the texture to attach
	 * @param layer
	 *            the layer of the texture to attach
	 * @return whether the operation succeeded
	 */
	protected boolean attachTextureLayer(Attachment attachment, Texture texture, int layer, int level) {
		return this.whileBound((FrameBuffer buffer) -> {
			glFramebufferTextureLayer(GL_FRAMEBUFFER, attachment.getGLEnum(), texture.getHandle(), level, layer);
			return !GLErrors.checkForError(TAG, "glFramebufferTextureLayer");
		});
	}

	/**
	 * Convenience method to remove an existing attachment.
	 * 
	 * @param attachment
	 *            the attachment to remove
	 * @return whether the operation succeeded
	 */
	protected boolean removeTextureAttachment(Attachment attachment) {
		return this.whileBound((FrameBuffer buffer) -> {
			glFramebufferTexture(GL_FRAMEBUFFER, attachment.getGLEnum(), 0, 0);
			return !GLErrors.checkForError(TAG, "glFrameBufferTexture(0)");
		});
	}

	/**
	 * Attaches a Texture2D to this framebuffer.
	 * 
	 * @param attachment
	 *            the attachment point to use
	 * @param texture
	 *            the texture to attach
	 * @return
	 */
	public boolean attachTexture(Attachment attachment, Texture2D texture) {
		boolean success = this.attachTexture2D(attachment, texture, 0);
		if (success) {
			this.attachments.put(attachment, texture);
		}
		return success;
	}

	/**
	 * Attaches a cube map face to this framebuffer.
	 *
	 * @param attachment
	 *            the attachment point to use
	 * @param texture
	 *            the texture to attach
	 * @param face
	 *            the face of the cubemap to attach
	 * @return
	 */
	public boolean attachTexture(Attachment attachment, CubeMap texture, CubeMap.Face face) {
		boolean success = this.attachCubeMapFace(attachment, texture, face, 0);
		if (success) {
			this.attachments.put(attachment, texture);
		}
		return success;
	}

	/**
	 * @param key
	 *            the attachment to look for
	 * @return the texture currently attached to that attachment point
	 */
	public Texture getAttachment(Attachment key) {
		return attachments.get(key);
	}

	/**
	 * Removes the given attachment from this framebuffer object.
	 * 
	 * @param key
	 *            the attachment to remove
	 * @return whether the operation succeeded
	 */
	public boolean removeAttachment(Attachment key) {
		boolean success = this.removeTextureAttachment(key);
		if (success) {
			attachments.remove(key);
		}
		return success;
	}
}
