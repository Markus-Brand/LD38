package mbeb.lifeforms;

import org.lwjgl.glfw.*;

import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class ChestBehaviour extends ReferenceEntityBehaviour {

	public ChestBehaviour(final PlayerEntity playerEntity) {
		super(playerEntity);
	}

	@Override
	public void update(final double deltaTime, final IEntity entity) {
		final PlayerEntity chest = (PlayerEntity) entity;

		chest.getAnimator().ensureRunning("Open", KeyBoard.isKeyDown(GLFW.GLFW_KEY_E));
	}

}
