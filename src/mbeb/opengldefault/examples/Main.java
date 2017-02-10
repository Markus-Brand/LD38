package mbeb.opengldefault.examples;

import mbeb.opengldefault.game.BunnyGame;
import mbeb.opengldefault.openglcontext.OpenGLContext;

/**
 * Test the application by showing a bunny
 */
public class Main {

	private static final String TAG = "Main";

	public static void main(String[] args) {
		new OpenGLContext(new BunnyGame(), args);
	}
}
