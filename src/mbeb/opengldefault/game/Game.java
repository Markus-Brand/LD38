package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;

import mbeb.opengldefault.camera.FirstPersonCamera;
import mbeb.opengldefault.main.GLErrors;
import mbeb.opengldefault.main.Main;
import mbeb.opengldefault.rendering.TextureUtils;

/**
 * Object to characterize a whole game
 */
public abstract class Game {
	/** Class Name Tag */
	private static final String TAG = "Game";

	protected FirstPersonCamera cam;

	public Game() {
		cam = new FirstPersonCamera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));
		
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}

	/**
	 * 
	 * @param deltaTime how much time passed since last frame
	 */
	public void update(double deltaTime) {

		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, Main.getWidth(), Main.getHeight());
		GLErrors.checkForError(TAG, "glViewport");


		cam.update();

		render();
		
	}
	
	/**
	 * show all your objects
	 */
	public abstract void render();

	public void clear() {
		TextureUtils.clearCache();
	}

}
