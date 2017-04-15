package mbeb.opengldefault.game;

import java.util.HashMap;

import java.util.Map;

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.gl.GLContext;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

/**
 * Abstract class to characterize a whole game
 */
public abstract class Game {

	private static final String TAG = "Game";

	/**
	 * Current GameStateIdentifier
	 */
	private GameStateIdentifier currentGameState;

	/**
	 * Mapping from the GameStateIdentifier enum to the actual GameState
	 */
	private Map<GameStateIdentifier, GameState> gameStates;

	/**
	 * Adds a GameStateIdentifier -> GameState mapping entry. The first GameState to add will be the startup entry per default
	 *
	 * @param key
	 * @param newGameState
	 */
	protected void addGameState(GameStateIdentifier key, GameState newGameState) {
		newGameState.init();
		if (gameStates == null) {
			gameStates = new HashMap<>();
			currentGameState = key;
			newGameState.open();
		}
		gameStates.put(key, newGameState);
	}

	/**
	 * Init the Game here. The OpenGL context is already created at this Point.
	 */
	public abstract void init();

	/**
	 * Entry Point for the update cycle
	 *
	 * @param deltaTime
	 *            time that passed since the last update
	 */
	public void update(double deltaTime) {
		GameState currentState = getCurrentGameState();
		currentState.update(deltaTime);
		if (!currentState.isActive()) {
			currentGameState = currentState.getNextState();
			currentState.resetNextGameState();
			if (currentGameState == GameStateIdentifier.EXIT) {
				GLContext.close();
			} else {
				getCurrentGameState().open();
			}
		}
	}

	/**
	 * Rendering entry point of an update cycle
	 */
	public void render() {
		GameState currentGameState = getCurrentGameState();
		if (currentGameState != null) {
			preGameStateRender();
			currentGameState.render();
		}
	}

	/**
	 * executed once before each render call on a gameSate
	 */
	protected void preGameStateRender() {
		glViewport(0, 0, OpenGLContext.getFramebufferWidth(), OpenGLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");

		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");
	}

	/**
	 * Getter for the current GameState
	 *
	 * @return the current GameState
	 */
	private GameState getCurrentGameState() {
		return gameStates.get(currentGameState);
	}

	/**
	 * Clear the Game. The game will close after this method is called.
	 */
	public void clear() {
		for (GameState state : gameStates.values()) {
			state.clear();
		}
	}
}
