package mbeb.opengldefault.game;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		addGameState(GameStateIdentifier.MAIN_MENU, new MainMenu());
		addGameState(GameStateIdentifier.BUNNY_GAME, new BunnyGameState());
		addGameState(GameStateIdentifier.BEZIER_FLIGHT, new FlightGame());
	}

	@Override
	public void clear() {
		super.clear();
	}

}
