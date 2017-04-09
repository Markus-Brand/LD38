package mbeb.opengldefault.game;


import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.rendering.textures.*;

import javax.swing.*;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		addGameState(GameStateIdentifier.MAIN_MENU, new MainMenu());
		addGameState(GameStateIdentifier.GAME, letUserPickGame());
	}
	
	private GameState letUserPickGame() {
		try {
			Class[] options = new Class[]{FlightGame.class, BunnyGameState.class};
			return ((Class<GameState>) JOptionPane.showInputDialog(
					null,
					"Choose a game",
					"OGL-Default init",
					JOptionPane.PLAIN_MESSAGE,
					null,
					options, options[0])).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			Log.error(TAG, "cannot create GameState", e);
			return null;
		}
	}

	@Override
	public void clear() {
		super.clear();
		TextureCache.clearCache();
	}

}
