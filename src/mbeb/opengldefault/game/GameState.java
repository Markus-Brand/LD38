package mbeb.opengldefault.game;

/**
 * Interface for a GameState.
 * GameStates can be MainMenus, the game itself, options, etc.
 * Each gameState should have a {@link GameStateIdentifier} to manage it in a {@link Game} class
 *
 * @author Markus
 */
public interface GameState {

	/**
	 * Init the GameState here. The OpenGL context is already created at this Point.
	 */
	void init();

	/**
	 * Entry Point for the update cycle
	 *
	 * @param deltaTime
	 *            time that passed since the last update
	 */
	void update(double deltaTime);

	/**
	 * Rendering entry point of an update cycle
	 */
	void render();

	/**
	 * Clear the GameState. The game will close after this method is called.
	 */
	void clear();

	/**
	 * Getter for the nextGameState
	 *
	 * @return the nextGameState
	 */
	GameStateIdentifier getNextState();

	/**
	 * The GameState is active if the nextGameState is null
	 *
	 * @return
	 */
	default boolean isActive() {
		return getNextState() == null;
	}

	/**
	 * Resets the nextGameState parameter to null
	 */
	void resetNextGameState();

	/**
	 * Gets called once if the GameState was just opened
	 */
	void open();
}
