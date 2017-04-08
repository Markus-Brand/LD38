package mbeb.opengldefault.game;

import mbeb.opengldefault.gl.texture.TextureLoader;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		addGameState(GameStateIdentifier.MAIN_MENU, new MainMenu());
		addGameState(GameStateIdentifier.GAME, new BunnyGameState());
	}

	@Override
	public void clear() {
		super.clear();
		TextureLoader.clearCache();
	}

}
