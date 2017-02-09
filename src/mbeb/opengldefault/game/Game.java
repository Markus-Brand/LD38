package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1i;

import org.joml.Vector3f;

import mbeb.opengldefault.camera.FirstPersonCamera;
import mbeb.opengldefault.main.GLErrors;
import mbeb.opengldefault.main.Main;
import mbeb.opengldefault.rendering.ScreenAlignedQuad;
import mbeb.opengldefault.rendering.Shader;
import mbeb.opengldefault.rendering.Texture;

public class Game {
	/** Class Name Tag */
	private static final String TAG = "Game";

	private Shader shader;
	private int texture;
	private FirstPersonCamera cam;

	public Game() {

		shader = new Shader("rect.vert", "rect.frag");
		shader.addUniformBlockIndex(1, "Matrices");

		cam = new FirstPersonCamera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));

		texture = Texture.loadTexture("AO.png");
	}

	public void update(double deltaTime) {

		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, Main.getWidth(), Main.getHeight());
		GLErrors.checkForError(TAG, "glViewport");

		shader.use();

		cam.update();

		glBindTexture(GL_TEXTURE_2D, texture);
		GLErrors.checkForError(TAG, "glBindTexture");
		glUniform1i(shader.getUniform("u_texture"), texture);
		GLErrors.checkForError(TAG, "glUniform1i");

		ScreenAlignedQuad.render();

	}

	public void clear() {
		Texture.clearCache();
	}

}
