package mbeb.opengldefault.game;

import mbeb.ld38.HealthBarGUI;
import mbeb.ld38.SharedData;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		final OptionsMenu options = new OptionsMenu();

		SharedData data = new SharedData(new HealthBarGUI(), null, null);

		addGameState(GameStateIdentifier.INTRO, new IntroGameState());
		addGameState(GameStateIdentifier.DUNGEON, new DungeonGameState(data));
		addGameState(GameStateIdentifier.OVERWORLD, new OverworldGameState(data));
		addGameState(GameStateIdentifier.OPTIONS, options);
		super.init();
	}

	@Override
	public void clear() {
		super.clear();
	}

}
