package mbeb.opengldefault.game;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		OptionsMenu options = new OptionsMenu();
		addGameState(GameStateIdentifier.MAIN_MENU, new MainMenu());
		addGameState(GameStateIdentifier.BUNNY_GAME, new BunnyGameState());
		addGameState(GameStateIdentifier.BEZIER_FLIGHT, new FlightGame());
		addGameState(GameStateIdentifier.OPTIONS, options);
		super.init();
	}

	@Override
	public void clear() {
		super.clear();
	}

}
