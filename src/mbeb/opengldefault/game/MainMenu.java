package mbeb.opengldefault.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import mbeb.opengldefault.gui.GUI;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.gui.elements.GUIElement;
import mbeb.opengldefault.gui.elements.TextGUIElement;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.gl.shader.ShaderProgram;

public class MainMenu implements GameState {
	private static final String TAG = "MainMenu";

	private TextGUI textGUI;

	private AtlasGUI menuGUI;

	private ShaderProgram guiShader;

	private TextGUIElement fps, buttonGame;

	private GUIElement buttonExit;

	private GameStateIdentifier nextGameState = null;

	private List<GameStateIdentifier> games = new ArrayList<>();
	private int selectedGame = 0;

	public MainMenu() {
		//Currently empty, because we can do everything in the init() method
	}

	@Override
	public void init() {
		menuGUI = new AtlasGUI(GUI.loadGUITexture("menu.png"), 4, 4);
		guiShader = new ShaderProgram("gui.vert", "gui.frag");
		textGUI = new TextGUI(new Font("Comic Sans MS", Font.PLAIN, 128));

		textGUI.setShader(guiShader);
		menuGUI.setShader(guiShader);

		fps = textGUI.addText("0", new Vector2f(), 0.03f);
		fps.setPositionRelativeToScreen(0, 0);
		fps.setColor(Color.ORANGE);

		buttonGame = textGUI.addText("Loading...", new Vector2f(), 0.2f);
		buttonGame.setPositionRelativeToScreen(0.5f, 0.5f);
		buttonExit = menuGUI.addAtlasGUIElement(0, new Vector2f(), new Vector2f(0.1f, GLContext.getAspectRatio() * 0.1f))
				.setPositionRelativeToScreen(0.01f, 0.99f);

		games.add(GameStateIdentifier.BUNNY_GAME);
		games.add(GameStateIdentifier.BEZIER_FLIGHT);
		selectedGame = games.size();
		selectNextGame();
	}

	@Override
	public void update(double deltaTime) {
		fps.setText("FPS: " + (int) (1 / deltaTime));
		menuGUI.update(deltaTime);
		textGUI.update(deltaTime);

		if (KeyBoard.isKeyDown(GLFW_KEY_ESCAPE)) {
			nextGameState = GameStateIdentifier.EXIT;
		}

		if (buttonGame.selected()) {
			buttonGame.setColor(Color.RED);
			if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
				startGame();
			}
		} else {
			buttonGame.setColor(Color.GREEN);
		}

		if (buttonExit.selected()) {
			buttonExit.setColor(Color.RED);
			if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
				nextGameState = GameStateIdentifier.EXIT;
			}
		} else {
			buttonExit.setColor(Color.GREEN);
		}

		if (KeyBoard.pullKeyDown(GLFW_KEY_TAB)) {
			selectNextGame();
		}
	}

	/**
	 * circulate, which gameState to start
	 */
	private void selectNextGame() {
		selectedGame++;
		if (selectedGame >= games.size()) {
			selectedGame = 0;
		}
		buttonGame.setText(games.get(selectedGame).toString());
		buttonGame.setPositionRelativeToScreen(0.5f, 0.5f);;
	}

	private void startGame() {
		nextGameState = games.get(selectedGame);
	}

	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, GLContext.getFramebufferWidth(), GLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");

		menuGUI.render();
		textGUI.render();
	}

	@Override
	public void clear() {
		//Currently nothing to do here (thanks Java GC)
	}

	@Override
	public void open() {
		GLContext.showCursor();
	}

	@Override
	public GameStateIdentifier getNextState() {
		return nextGameState;
	}

	@Override
	public void resetNextGameState() {
		nextGameState = null;
	}
}
