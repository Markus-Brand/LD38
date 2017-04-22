package mbeb.opengldefault.game;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		OptionsMenu options = new OptionsMenu();
		addGameState(GameStateIdentifier.DUNGEON, new DungeonGameState());
		addGameState(GameStateIdentifier.INTRO, new IntroGameState());
		addGameState(GameStateIdentifier.OVERWORLD, new OverworldGameState());
		addGameState(GameStateIdentifier.OPTIONS, options);
		super.init();
	}

	@Override
	public void clear() {
		super.clear();
	}

}
