package mbeb.opengldefault.gl.framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

import java.util.EnumMap;
import java.util.Map;

import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.gl.texture.CubeMap;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.gl.texture.Texture2DArray;
import mbeb.opengldefault.gl.texture.Texture3D;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.ContextBindings;

/**
 * Represents a frame buffer object in OpenGL.
 */
public class FrameBuffer extends GLObject {
	private static final String TAG = "FrameBuffer";

	/**
	 * The target OpenGL can draw to or read from.
	 */
	public enum Target {
		FRONT(GL_FRONT_LEFT), LEFT(GL_FRONT_LEFT), BACK(GL_BACK_LEFT), RIGHT(GL_FRONT_RIGHT), FRONT_RIGHT(GL_FRONT_RIGHT), FRONT_LEFT(GL_FRONT_LEFT), BACK_RIGHT(GL_BACK_RIGHT),
		BACK_LEFT(GL_BACK_LEFT), COLOR0(GL_COLOR_ATTACHMENT0), COLOR1(GL_COLOR_ATTACHMENT1), COLOR2(GL_COLOR_ATTACHMENT2), COLOR3(GL_COLOR_ATTACHMENT3), COLOR4(GL_COLOR_ATTACHMENT4),
		COLOR5(GL_COLOR_ATTACHMENT5), COLOR6(GL_COLOR_ATTACHMENT6), COLOR7(GL_COLOR_ATTACHMENT7);

		private int glEnum;

		Target(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this target
		 */
		public int getGLEnum() {
			return glEnum;

		}
	}

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
	 * Convenience method for glFramebufferTexture.
	 * This will attach a texture with all its possible layers.
	 *
	 * @param attachment
	 *            the attachment target
	 * @param texture
	 *            the texture to attach
	 * @param level
	 *            the level of the texture to attach
	 * @return whether the operation succeeded
	 */
	protected boolean attachTexture(Attachment attachment, Texture texture, int level) {
		return this.whileBound((FrameBuffer buffer) -> {
			glFramebufferTexture(GL_FRAMEBUFFER, attachment.getGLEnum(), texture.getHandle(), level);
			return !GLErrors.checkForError(TAG, "glFramebufferTexture");
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
	protected boolean attachTextureLayer(Attachment attachment, Texture2DArray texture, int layer, int level) {
		return this.whileBound((FrameBuffer buffer) -> {
			glFramebufferTextureLayer(GL_FRAMEBUFFER, attachment.getGLEnum(), texture.getHandle(), level, layer);
			return !GLErrors.checkForError(TAG, "glFramebufferTextureLayer");
		});
	}

	/**
	 * Convenience method for glFramebufferTexture3D.
	 *
	 * @param attachment
	 *            the attachment target
	 * @param texture
	 *            the texture to attach
	 * @param level
	 *            the level of the texture to attach
	 * @param z
	 *            the z coordinate of the layer of the texture to attach
	 * @return whether the operation succeeded
	 */
	protected boolean attachTexture3D(Attachment attachment, Texture3D texture, int z, int level) {
		return this.whileBound((FrameBuffer buffer) -> {
			glFramebufferTexture3D(GL_FRAMEBUFFER, attachment.getGLEnum(), texture.getType().getGLEnum(), texture.getHandle(), level, z);
			return !GLErrors.checkForError(TAG, "glFramebufferTexture3D");
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
	 * Attaches a texture to this framebuffer.
	 * If this texture is an array of 1D or 2D textures, a 3D texture or a cube map, the
	 * 
	 * @param attachment
	 *            the attachment point to use
	 * @param texture
	 *            the texture to attach
	 * @return whether the operation succeeded
	 */
	public boolean attach(Attachment attachment, Texture texture) {
		boolean success = this.attachTexture(attachment, texture, 0);
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
	 * @return whether the operation succeeded
	 */
	public boolean attach(Attachment attachment, CubeMap texture, CubeMap.Face face) {
		boolean success = this.attachCubeMapFace(attachment, texture, face, 0);
		if (success) {
			this.attachments.put(attachment, texture);
		}
		return success;
	}

	/**
	 * Attaches a layer of the given array to this framebuffer.
	 *
	 * @param attachment
	 *            the attachment point to use
	 * @param texture
	 *            the texture to attach
	 * @param layer
	 *            the layer of the array to attach
	 * @return whether the operation succeeded
	 */
	public boolean attach(Attachment attachment, Texture2DArray texture, int layer) {
		boolean success = this.attachTextureLayer(attachment, texture, layer, 0);
		if (success) {
			this.attachments.put(attachment, texture);
		}
		return success;
	}

	/**
	 * Attaches a layer of the given 3D texture to this framebuffer.
	 *
	 * @param attachment
	 *            the attachment point to use
	 * @param texture
	 *            the texture to attach
	 * @param z
	 *            the z coordinate of the layer to attach
	 * @return whether the operation succeeded
	 */
	public boolean attach(Attachment attachment, Texture3D texture, int z) {
		boolean success = this.attachTexture3D(attachment, texture, z, 0);
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
