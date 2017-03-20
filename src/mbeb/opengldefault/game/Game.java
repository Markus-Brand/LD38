package mbeb.opengldefault.game;

import java.util.HashMap;

import java.util.Map;

import mbeb.opengldefault.openglcontext.OpenGLContext;

/**
 * Abstract class to characterize a whole game
 */
public abstract class Game {

	/**
	 * Current GameState
	 */
	private GameStates currentGameState;

	/**
	 * Mapping from the GamaStates enum to the actual GameState
	 */
	private Map<GameStates, GameState> gameStates;

	/**
	 * Adds a GameStates -> GameState mapping entry. The first GameState to add will be the startup entry per default
	 *
	 * @param key
	 * @param newGameState
	 */
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

	/**
	 * Getter for the current GameState
	 *
	 * @return the current GameState
	 */
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
