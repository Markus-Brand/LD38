package mbeb.opengldefault.game;

import mbeb.opengldefault.options.OptionsMenu;
import mbeb.opengldefault.rendering.textures.*;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		addGameState(GameStateIdentifier.MAIN_MENU, new MainMenu());
		addGameState(GameStateIdentifier.GAME, new BunnyGameState());	
		addGameState(GameStateIdentifier.OPTIONS, new OptionsMenu());	
		super.init();	
	}

	@Override
	public void clear() {
		super.clear();
		TextureCache.clearCache();
	}

}
