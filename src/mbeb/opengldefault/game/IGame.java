package mbeb.opengldefault.game;

public interface IGame {
	/**
	 * Init the Game here. The OpenGL context is already created at this Point.
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
	 * Rendering entry point of a update cycle
	 */
	void render();

	/**
	 * Clear the Game. The game will close after this method is called.
	 */
	void clear();
}
