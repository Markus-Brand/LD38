package mbeb.opengldefault.logging;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GLErrors {

	/** Class Name Tag */
	private static final String TAG = "GLErrors";

	/**
	 * Checks if an error occurred between this moment and the last error check.
	 * This method should be called every time an OpenGL method is called.
	 *
	 * @param classTag
	 *            Name of the class the method was called from
	 * @param message
	 *            additional information. Should be the name of the OpenGL that was called and could have caused the error
	 * @return whether an error occurred
	 */
	public static boolean checkForError(String classTag, String message) {
		return checkForError(classTag, message, false);
	}

	/**
	 * Checks if an error occurred between this moment and the last error check.
	 * This method should be called every time an OpenGL method is called.
	 *
	 * @param classTag
	 *            Name of the class the method was called from
	 * @param message
	 *            additional information. Should be the name of the OpenGL that was called and could have caused the error
	 * @param abortProgram abort the current thread by throwing an exception
	 * @return whether an error occurred
	 */
	public static boolean checkForError(String classTag, String message, boolean abortProgram) {
		int error = glGetError();
		if (error == GL_NO_ERROR) {
			return false;
		} else {
			GLException ex = new GLException(classTag, message, error);
			if (abortProgram) {
				throw ex;
			} else {
				ex.printStackTrace();
			}
			Log.error(classTag, ex.getMessage());
			return true;
		}
	}

	/**
	 * a small class to format gl-errors nicely
	 */
	private static final class GLException extends RuntimeException {

		public GLException(String classTag, String method, int error) {
			super(format(classTag, method, error));
		}

		private static String format(String classTag, String method, int error) {
			String errorMessage = classTag + ">>" + method + ": caused error code " + error + " (";
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
				default:
					errorMessage += "UNKNOWN_EXCEPTION"; 
			}
			errorMessage += ")";
			return errorMessage;
		}
	}
}
