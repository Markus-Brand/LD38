package mbeb.opengldefault.game;

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
	 * Rendering entry point of a update cycle
	 */
	public abstract void render();

	/**
	 * Clear the GameState. The game will close after this method is called.
	 */
	public abstract void clear();

	public abstract GameStates getNextState();

	public default boolean isActive() {
		return getNextState() == null;
	}

	public abstract void resetNextGameState();

	public abstract void open();
}
