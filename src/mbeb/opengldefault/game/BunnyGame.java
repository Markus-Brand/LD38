package mbeb.opengldefault.game;

import mbeb.ld38.HealthBarGUI;
import mbeb.ld38.SharedData;
import mbeb.ld38.overworld.OverWorld;

public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	@Override
	public void init() {
		final OptionsMenu options = new OptionsMenu();

		SharedData data = new SharedData(new HealthBarGUI());
		data.overworld = new OverWorld();

		addGameState(GameStateIdentifier.INTRO, new IntroGameState(data));
		addGameState(GameStateIdentifier.OVERWORLD, new OverworldGameState(data));
		addGameState(GameStateIdentifier.DUNGEON, new DungeonGameState(data));
		super.init();
	}

	@Override
	public void clear() {
		super.clear();
	}

}
