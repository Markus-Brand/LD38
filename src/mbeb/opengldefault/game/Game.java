package mbeb.opengldefault.game;

import java.util.HashMap;
import java.util.Map;

import mbeb.opengldefault.openglcontext.OpenGLContext;

public abstract class Game {

	private GameStates currentGameState;

	private Map<GameStates, GameState> gameStates;

	protected void addGameState(GameStates key, GameState newGameState) {
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
		GameState currentState = getCurrentState();
		currentState.update(deltaTime);
		if (!currentState.isActive()) {
			currentGameState = currentState.getNextState();
			currentState.resetNextGameState();
			if (currentGameState == GameStates.EXIT) {
				OpenGLContext.close();
			} else {
				getCurrentState().open();
			}
		}
	}

	/**
	 * Rendering entry point of a update cycle
	 */
	public void render() {
		GameState currentState = getCurrentState();
		if (currentState != null) {
			currentState.render();
		}
	}

	private GameState getCurrentState() {
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
