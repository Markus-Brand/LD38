package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.FloatBuffer;

import mbeb.opengldefault.camera.FirstPersonCamera;
import mbeb.opengldefault.main.GLErrors;
import mbeb.opengldefault.main.Main;
import mbeb.opengldefault.rendering.Texture;
import mbeb.opengldefault.rendering.TextureUtils;
import mbeb.opengldefault.scene.DataFragment;
import mbeb.opengldefault.scene.ObjectLoader;
import mbeb.opengldefault.scene.TexturedRenderable;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

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
		Vector3f pos = cam.getPosition();
		bunny.getShader().use();
		GL20.glUniform3f(bunny.getShader().getUniform("viewPos"), pos.x, pos.y, pos.z);
		GLErrors.checkForError(TAG, "glUniform3f");
		Matrix4f model = new Matrix4f();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		GL20.glUniformMatrix4fv(bunny.getShader().getUniform("model"), false, model.get(buffer));
		GLErrors.checkForError(TAG, "glUniformMatrix4fv");
		bunny.render();
	}

	@Override
	public void clear() {
		TextureUtils.clearCache();
	}

}
