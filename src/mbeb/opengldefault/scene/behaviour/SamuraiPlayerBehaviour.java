package mbeb.opengldefault.scene.behaviour;

import org.lwjgl.glfw.GLFW;

import mbeb.lifeforms.PlayerEntity;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.scene.entities.IEntity;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

public class SamuraiPlayerBehaviour implements IBehaviour {

	@Override
	public void update(double deltaTime, IEntity entity) {
		PlayerEntity player = (PlayerEntity) entity;

		player.getAnimator().ensureRunning("Idle");
		player.getAnimator().ensureRunning("Jogging", KeyBoard.isKeyDown(GLFW.GLFW_KEY_W));
		player.getAnimator().ensureRunning("JoggingBack", KeyBoard.isKeyDown(GLFW_KEY_S));
		if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			player.startStroke();
		}

	}

}
