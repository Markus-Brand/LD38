package mbeb.opengldefault.scene.behaviour;

import org.lwjgl.glfw.GLFW;

import mbeb.lifeforms.PlayerEntity;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.scene.entities.IEntity;

public class SamuraiPlayerBehaviour implements IBehaviour {

	@Override
	public void update(double deltaTime, IEntity entity) {
		PlayerEntity player = (PlayerEntity) entity;

		player.getAnimator().ensureRunning("Idle");
		player.getAnimator().ensureRunning("Jogging", KeyBoard.isKeyDown(GLFW.GLFW_KEY_W));
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_Q)) {
			player.startStroke();
		}
		player.getAnimator().ensureRunning("Pierce", player.getSword().isStriking(), true);

	}

}
