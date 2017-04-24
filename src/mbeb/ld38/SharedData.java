package mbeb.ld38;

import mbeb.lifeforms.Player;
import mbeb.lifeforms.PlayerEntity;
import mbeb.opengldefault.rendering.renderable.IRenderable;

/**
 * things that each gameState / scene needs to have
 */
public class SharedData {

	public HealthBarGUI healthBarGUI;
	public PlayerEntity playerEntity;
	public IRenderable island;

	public SharedData(HealthBarGUI healthBarGUI) {
		this.healthBarGUI = healthBarGUI;
	}
}
