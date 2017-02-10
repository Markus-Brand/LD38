package mbeb.opengldefault.controls;

import org.joml.Vector2f;

public class Mouse {

	/** Class Name Tag */
	private static final String TAG = "Mouse";

	private static Vector2f cursorPos;
	private static boolean[] mouseDown;

	static {
		cursorPos = new Vector2f();
		mouseDown = new boolean[32];
	}

	public static void setPos(double xPos, double yPos) {
		cursorPos.x = (float) xPos;
		cursorPos.y = (float) yPos;
	}

	public static void buttonDown(int button) {
		mouseDown[button] = true;
	}

	public static void buttonUp(int button) {
		mouseDown[button] = false;
	}

	public static Vector2f getPos() {
		return cursorPos;
	}
	
	public static boolean isDown(int button) {
		return mouseDown[button];
	}

}
