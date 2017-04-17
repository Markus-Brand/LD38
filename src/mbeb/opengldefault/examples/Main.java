package mbeb.opengldefault.examples;

import mbeb.opengldefault.game.*;
import mbeb.opengldefault.gl.GLContext;

/**
 * Test the application by showing a bunny
 */
public class Main {

	private static final String TAG = "Main";

	private Main() {
		//should never be instantiated
	}

	public static void main(String[] args) {
		GLContext.startGame(new BunnyGame(), args);
	}
}
