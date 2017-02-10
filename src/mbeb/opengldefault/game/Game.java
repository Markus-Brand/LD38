package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;

import mbeb.opengldefault.camera.FirstPersonCamera;
import mbeb.opengldefault.main.GLErrors;
import mbeb.opengldefault.main.Main;
import mbeb.opengldefault.rendering.Texture;
import mbeb.opengldefault.rendering.TextureUtils;
import mbeb.opengldefault.scene.DataFragment;
import mbeb.opengldefault.scene.ObjectLoader;
import mbeb.opengldefault.scene.TexturedRenderable;

/**
 * Object to characterize a whole game
 */
public class Game implements IGame {
	/** Class Name Tag */
	private static final String TAG = "Game";

	protected FirstPersonCamera cam;

	TexturedRenderable bunny;

	public Game() {
	}

	@Override
	public void init() {
		bunny = new TexturedRenderable(new ObjectLoader().loadFromFile("bunny.obj", new DataFragment[] { DataFragment.POSITION, DataFragment.NORMAL, DataFragment.UV }), new Texture("bunny_2d.png"));
		cam = new FirstPersonCamera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}

	@Override
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

	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		TextureUtils.clearCache();
	}

}
