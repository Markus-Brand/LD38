package mbeb.opengldefault.controls;

public class KeyBoard {

	/** Class Name Tag */
	private static final String TAG = "KeyBoard";
	
	private static final int MAX_NUM_KEYS = 1024;

	/**
	 * Boolean array of all of the keys
	 */
	private static boolean[] keys;

	static {
		keys = new boolean[MAX_NUM_KEYS];
	}

	private KeyBoard() {
		//should never be instantiated
	}

	/**
	 * Gets called every time a key is pressed on the Keyboard.
	 * The method then updates the key with the specified keyCode in the key array
	 *
	 * @param keyCode
	 *            GLWF keyCode of the key
	 */
	public static void keyDown(int keyCode) {
		if (keyCode < 0 || keyCode >= keys.length) {
			return;
		}
		keys[keyCode] = true;
	}

	/**
	 * Gets called every time a key is released on the Keyboard.
	 * The method then updates the key with the specified keyCode in the key array
	 *
	 * @param keyCode
	 *            GLWF keyCode of the key
	 */
	public static void keyUp(int keyCode) {
		if (keyCode < 0 || keyCode >= keys.length) {
			return;
		}
		keys[keyCode] = false;
	}

	/**
	 * returns true if the keyCode key is pressed at the moment and false if the key is not pressed or if the keyCode is
	 * outside of the Array
	 *
	 * @param keyCode
	 *            GLWF keyCode of the key
	 * @return
	 */
	public static boolean isKeyDown(int keyCode) {
		if (keyCode < 0 || keyCode >= keys.length) {
			return false;
		}
		return keys[keyCode];
	}

	/**
	 * "pull a key-event"
	 *
	 * returns true if the keyCode key is pressed at the moment and false if the key is not pressed or if the keyCode is
	 * outside of the Array.
	 * After this method returns, the key is not marked as down anymore
	 *
	 * @param keyCode
	 *            GLWF keyCode of the key
	 * @return
	 */
	public static boolean pullKeyDown(int keyCode) {
		boolean isDown = isKeyDown(keyCode);
		keyUp(keyCode);
		return isDown;
	}

	public static void releaseAll() {
		keys = new boolean[MAX_NUM_KEYS];
	}
}
