package mbeb.opengldefault.examples;

import mbeb.opengldefault.game.*;
import mbeb.opengldefault.openglcontext.*;

/**
 * Test the application by showing a bunny
 */
public class Main {

	private static final String TAG = "Main";

	private Main() {
		//should never be instantiated
	}

	public static void main(String[] args) {
		OpenGLContext.startGame(new BunnyGame(), args);
	}
}
