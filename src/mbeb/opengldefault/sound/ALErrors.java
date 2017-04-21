package mbeb.opengldefault.sound;

import mbeb.opengldefault.logging.Log;

import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.alGetError;

public class ALErrors {

	/** Class Name Tag */
	private static final String TAG = "ALErrors";

	private ALErrors() {
		//should never be instantiated
	}

	/**
	 * Checks if an error occurred between this moment and the last error check.
	 * This method should be called every time an OpenAL method is called.
	 *
	 * @param classTag
	 *            Name of the class the method was called from
	 * @param message
	 *            additional information. Should be the name of the OpenAL that was called and could have caused the
	 *            error
	 * @return whether an error occurred
	 */
	public static boolean checkForError(String classTag, String message) {
		return checkForError(classTag, message, false);
	}

	/**
	 * Checks if an error occurred between this moment and the last error check.
	 * This method should be called every time an OpenAL method is called.
	 *
	 * @param classTag
	 *            Name of the class the method was called from
	 * @param message
	 *            additional information. Should be the name of the OpenAL that was called and could have caused the
	 *            error
	 * @param abortProgram
	 *            abort the current thread by throwing an exception
	 * @return whether an error occurred
	 */
	public static boolean checkForError(String classTag, String message, boolean abortProgram) {
		int error = alGetError();
		if (error == AL_NO_ERROR) {
			return false;
		} else {
			String errorMessage = classTag + ">>" + message + ": caused error code " + error;
			RuntimeException ex = new RuntimeException(errorMessage);
			if (abortProgram) {
				throw ex;
			}
			Log.error(classTag, message, ex);
			return true;
		}
	}
}
