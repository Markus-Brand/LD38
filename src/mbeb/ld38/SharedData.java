package mbeb.ld38;

import mbeb.lifeforms.Player;
import mbeb.opengldefault.rendering.renderable.IRenderable;

/**
 * things that each gameState / scene needs to have
 */
public class SharedData {

	public HealthBarGUI healthBarGUI;
	public Player player;
	public IRenderable island;

	public SharedData(HealthBarGUI healthBarGUI, Player player, IRenderable island) {
		this.healthBarGUI = healthBarGUI;
		this.player = player;
		this.island = island;
	}
}
