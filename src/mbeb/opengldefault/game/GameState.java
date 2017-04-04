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
	public abstract void init();

	/**
	 * Entry Point for the update cycle
	 *
	 * @param deltaTime
	 *            time that passed since the last update
	 */
	public abstract void update(double deltaTime);

	/**
	 * Rendering entry point of an update cycle
	 */
	public abstract void render();

	/**
	 * Clear the GameState. The game will close after this method is called.
	 */
	public abstract void clear();

	/**
	 * Getter for the nextGameState
	 *
	 * @return the nextGameState
	 */
	public abstract GameStateIdentifier getNextState();

	/**
	 * The GameState is active if the nextGameState is null
	 *
	 * @return
	 */
	public default boolean isActive() {
		return getNextState() == null;
	}

	/**
	 * Resets the nextGameState parameter to null
	 */
	public abstract void resetNextGameState();

	/**
	 * Gets called once if the GameState was just opened
	 */
	public abstract void open();
}
