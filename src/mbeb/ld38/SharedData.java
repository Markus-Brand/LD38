package mbeb.ld38;

import mbeb.ld38.overworld.OverWorld;
import mbeb.lifeforms.PlayerEntity;

/**
 * things that each gameState / scene needs to have
 */
public class SharedData {

	public HealthBarGUI healthBarGUI;
	public PlayerEntity playerEntity;
	public OverWorld overworld;

	public SharedData(HealthBarGUI healthBarGUI) {
		this.healthBarGUI = healthBarGUI;
	}
}
