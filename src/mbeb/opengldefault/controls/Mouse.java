package mbeb.opengldefault.controls;

import mbeb.opengldefault.openglcontext.OpenGLContext;

import org.joml.*;

public class Mouse {

	/** Class Name Tag */
	private static final String TAG = "Mouse";

	private Mouse() {
		//should never be instantiated
	}

	private static Vector2f cursorPos;
	private static boolean[] mouseDown;

	static {
		cursorPos = new Vector2f();
		mouseDown = new boolean[32];
	}

	/**
	 * update the cursors position
	 *
	 * @param xPos
	 * @param yPos
	 */
	public static void setPos(double xPos, double yPos) {
		cursorPos.x = (float) xPos;
		cursorPos.y = (float) yPos;
	}

	/**
	 * save a buttonDown-event
	 *
	 * @param button
	 *            the mouseButton-id
	 */
	public static void buttonDown(int button) {
		mouseDown[button] = true;
	}

	/**
	 * save a buttonUp-event
	 *
	 * @param button
	 *            the mouseButton-id
	 */
	public static void buttonUp(int button) {
		mouseDown[button] = false;
	}

	/**
	 * @return the current cursor position
	 */
	public static Vector2f getPos() {
		return cursorPos;
	}

	/**
	 * @param button
	 * @return true when mouseButton with given id is down currently
	 */
	public static boolean isDown(int button) {
		return mouseDown[button];
	}

	/**
	 * Returns the mouse position in normalized device coordinates ([-1, 1], [-1, 1])
	 * @return the mouse position in normalized device coordinates
	 */
	public static Vector2f getNormalizedDeviceCoordinates() {
		return OpenGLContext.getNDC(getPos());
	}
}
