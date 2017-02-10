package mbeb.opengldefault.logging;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GLErrors {

	/** Class Name Tag */
	private static final String TAG = "GLErrors";

	/**
	 * Checks if a error occured between this moment and the last error check. This method should be called every time a OpenGL method is called.
	 *
	 * @param classTag
	 *            Name of the class the method was called from
	 * @param message
	 *            additional information. Should be the name of the OpenGL that was called and could have caused the error
	 * @return true if a error occured
	 */
	public static boolean checkForError(String classTag, String message) {
		int error = glGetError();
		if (error == GL_NO_ERROR) {
			return false;
		} else {
			String errorMessage = message + ": caused error code " + error + " (";
			switch (error) {
				case GL_INVALID_ENUM:
					errorMessage += "GL_INVALID_ENUM";
					break;
				case GL_INVALID_VALUE:
					errorMessage += "GL_INVALID_VALUE";
					break;
				case GL_INVALID_OPERATION:
					errorMessage += "GL_INVALID_OPERATION";
					break;
				case GL_INVALID_FRAMEBUFFER_OPERATION:
					errorMessage += "GL_INVALID_FRAMEBUFFER_OPERATION";
					break;
				case GL_OUT_OF_MEMORY:
					errorMessage += "GL_OUT_OF_MEMORY";
					break;

			}
			errorMessage += ")";
			Log.error(classTag, errorMessage);
			return true;
		}
	}
}
