package game;

import static org.lwjgl.opengl.GL11.*;
import main.GLErrors;
import rendering.ScreenAlignedQuad;
import rendering.Shader;

public class Game {
	/** Class Name Tag */
	private static final String TAG = "Game";

	private Shader shader;

	public Game() {
		shader = new Shader("rect.vert", "rect.frag");
	}

	public void update(double deltaTime) {

		glClearColor(0.05f, 0.075f, 0.075f, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glViewport(0, 0, 1920, 1080);

		shader.use();

		ScreenAlignedQuad.render();

		GLErrors.checkForError(TAG, "glDrawElements");
	}

}
