package mbeb.opengldefault.gui.menu;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

import org.joml.Vector2f;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.game.GameState;
import mbeb.opengldefault.game.GameStates;
import mbeb.opengldefault.gui.AtlasGUIElement;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.shader.Shader;

public class MainMenu implements GameState {
	private static final String TAG = "MainMenu";

	private TextGUI textGUI;

	private Menu menuGUI;

	private Shader guiShader;

	GameStates nextGameState = null;

	public MainMenu() {
		menuGUI = new Menu("gui.png");
		guiShader = new Shader("gui.vert", "gui.frag");
		textGUI = new TextGUI(32, 16, "font.png");
	}

	@Override
	public void init() {
		textGUI.addTextElement("Hallo Welt! ! !012412", new Vector2f(), 0.05f).setPositionRelativeToScreen(0.5f, 0.5f);
		textGUI.addTextElement("Und Hallo Erik!", new Vector2f(-1, -0.2f), 0.02f);
		menuGUI.addButtonElement(
				new AtlasGUIElement(0, 2, new Vector2f(0.5f, OpenGLContext.getAspectRatio() * 0.5f))
						.setPositionRelativeToScreen(
								0.25f, 0.5f), GameStates.GAME);
		menuGUI.addButtonElement(
				new AtlasGUIElement(2, 2, new Vector2f(0.5f, OpenGLContext.getAspectRatio() * 0.5f))
						.setPositionRelativeToScreen(
								0.75f, 0.5f), GameStates.EXIT);
	}

	@Override
	public void update(double deltaTime) {
		menuGUI.update(deltaTime);

		if (KeyBoard.isKeyDown(GLFW_KEY_ESCAPE)) {
			nextGameState = GameStates.EXIT;
		}
	}

	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getFramebufferWidth(), OpenGLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");

		guiShader.use();
		menuGUI.render(guiShader);
		textGUI.render(guiShader);
	}

	@Override
	public void clear() {

	}

	@Override
	public void open() {
		OpenGLContext.showCursor();
	}

	@Override
	public GameStates getNextState() {
		if (nextGameState != null) {
			return nextGameState;
		} else {
			return menuGUI.getNextState();
		}
	}

	@Override
	public void resetNextGameState() {
		nextGameState = null;
		menuGUI.resetNextGameState();
	}
}
