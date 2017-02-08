package mbeb.opengldefault.main;

import org.joml.Vector2f;

public class Mouse {

	private static Vector2f cursorPos;

	static {
		cursorPos = new Vector2f();
	}

	public static void setPos(double xPos, double yPos) {
		cursorPos.x = (float) xPos;
		cursorPos.y = (float) yPos;
	}

	public static void buttonDown(int button) {
		// TODO Auto-generated method stub

	}

	public static void buttonUp(int button) {
		// TODO Auto-generated method stub

	}

	public static Vector2f getPos() {
		return cursorPos;
	}

}
