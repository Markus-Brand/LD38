package mbeb.opengldefault.controls;

import mbeb.opengldefault.gl.GLContext;

import org.joml.*;

public class Mouse {

	/** Class Name Tag */
	private static final String TAG = "Mouse";

	/** Position of the Mouse cursor in pixels */
	private static Vector2f cursorPos;

	/** boolean array that saves the state of each Mouse button (Pressed or released) */
	private static boolean[] mouseDown;

	/** The Mouse focus */
	private static IFocusable focus;

	/** max numbers of buttons on the mouse */
	private static final int NUM_MOUSE_BUTTONS = 32;

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

	/**
	 * Release all pressed Mouse Buttons
	 */
	public static void releaseAll() {
		mouseDown = new boolean[NUM_MOUSE_BUTTONS];
	}

	/**
	 * Called by IFocusables that want to gain the current Focus.
	 * It will be able to get this focus, if there is no current focus or if the current IFocusable does not want to
	 * keep Focus
	 *
	 * @param focus
	 * @return
	 */
	public static boolean requestFocus(IFocusable focus) {
		if (getFocus() == null || !getFocus().keepFocus()) {
			setFocus(focus);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Getter for the current Mouse focus
	 *
	 * @return the focus
	 */
	public static IFocusable getFocus() {
		return focus;
	}

	/**
	 * Setter for the current Mouse focus
	 *
	 * @param focus
	 *            the new Focus
	 *            the focus to set
	 */
	private static void setFocus(IFocusable focus) {
		if (focus == null) {
			resetFocus();
		} else if (focus != Mouse.focus) {
			if (Mouse.focus != null) {
				Mouse.focus.releasedFocus();
			}
			focus.gotFocus();
			Mouse.focus = focus;
		}
	}

	/**
	 * Resets the current focus and notifies it of this
	 */
	private static void resetFocus() {
		if (Mouse.focus != null) {
			Mouse.focus.releasedFocus();
			Mouse.focus = null;
		}
	}

	/**
	 * Releases the focus, if the given IFocusable is the current focus
	 *
	 * @param focus
	 *            the IFocusable to release
	 */
	public static void releaseFocus(IFocusable focus) {
		if (focus.equals(getFocus())) {
			focus.releasedFocus();
			resetFocus();
		}
	}
}
