package mbeb.opengldefault.game;

import mbeb.opengldefault.openglcontext.*;

public abstract class Game {

	private OpenGLContext context = null;

	public OpenGLContext getContext() {
		return context;
	}

	public void setContext(OpenGLContext context) {
		this.context = context;
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
	public abstract void update(double deltaTime);

	/**
	 * Rendering entry point of a update cycle
	 */
	public abstract void render();

	/**
	 * Clear the Game. The game will close after this method is called.
	 */
	public abstract void clear();
}
