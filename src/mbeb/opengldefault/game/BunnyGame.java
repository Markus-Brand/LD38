package mbeb.opengldefault.game;

import mbeb.opengldefault.rendering.textures.*;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		addGameState(GameStates.MAIN_MENU, new MainMenu());
		addGameState(GameStates.GAME, new BunnyGameState());
	}

	@Override
	public void clear() {
		super.clear();
		TextureCache.clearCache();
	}

}
