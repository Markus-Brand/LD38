package mbeb.opengldefault.gl.framebuffer;

import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.ContextBindings;

import static org.lwjgl.opengl.GL30.*;

/**
 * Represents a framebuffer object in OpenGL.
 */
public class FrameBuffer extends GLObject {
	private static final String TAG = "FrameBuffer";

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
		if(!success) {
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
