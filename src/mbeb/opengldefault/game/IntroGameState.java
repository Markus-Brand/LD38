package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.logging.GLErrors;

public class IntroGameState implements GameState {

	private static final String TAG = "IntroGameState";

	private float progress;

	@Override
	public void init() {
		progress = 0;
	}

	@Override
	public void update(double deltaTime) {
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			progress = 1;
		}
		progress += deltaTime;
	}

	@Override
	public void render() {
		glClearColor(progress, progress, progress, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, GLContext.getFramebufferWidth(), GLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");
	}

	@Override
	public GameStateIdentifier getNextState() {
		return progress >= 1 ? GameStateIdentifier.OVERWORLD : null;
	}

	@Override
	public void resetNextGameState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void open() {
		progress = 0;
	}

}
