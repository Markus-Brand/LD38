package mbeb.opengldefault.controls;

import mbeb.opengldefault.gl.GLContext;

import org.joml.*;

public class Mouse {

	/** Class Name Tag */
	private static final String TAG = "Mouse";

	private static Vector2f cursorPos;
	private static boolean[] mouseDown;

	private static IFocusable focus;

	static {
		cursorPos = new Vector2f();
		releaseAll();
	}

	private Mouse() {
		//should never be instantiated
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
	 *
	 * @return the mouse position in normalized device coordinates
	 */
	public static Vector2f getNormalizedDeviceCoordinates() {
		return GLContext.getNDC(getPos());
	}

	public static void releaseAll() {
		mouseDown = new boolean[32];
	}

	public static boolean requestFocus(IFocusable focus) {
		if (getFocus() == null || !getFocus().keepFocus()) {
			setFocus(focus);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the focus
	 */
	public static IFocusable getFocus() {
		return focus;
	}

	/**
	 * @param focus
	 *            the focus to set
	 */
	public static void setFocus(IFocusable focus) {
		if (focus != Mouse.focus && focus != null) {
			if (Mouse.focus != null) {
				Mouse.focus.releasedFocus();
			}
			focus.gotFocus();
		}
		Mouse.focus = focus;
	}

	public static void releaseFocus(IFocusable focus) {
		if (focus.equals(getFocus())) {
			focus.releasedFocus();
			setFocus(null);
		}
	}
}
