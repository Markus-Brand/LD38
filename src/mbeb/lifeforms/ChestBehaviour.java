package mbeb.lifeforms;

import java.util.function.*;

import org.lwjgl.glfw.*;

import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class ChestBehaviour extends ReferenceEntityBehaviour {

	boolean alreadyOpened;
	boolean started;
	float openingTime;
	float timePassed;
	Consumer<ChestEntity> consumer;

	public ChestBehaviour(final PlayerEntity playerEntity, final Consumer<ChestEntity> consumer) {
		super(playerEntity);
		alreadyOpened = false;
		started = false;
		timePassed = 0;
		openingTime = .99f;
		this.consumer = consumer;
	}

	@Override
	public void update(final double deltaTime, final IEntity entity) {
		final ChestEntity chest = (ChestEntity) entity;

		if (!started) {
			if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_E)) {
				started = true;
			}
		} else {
			if (timePassed < openingTime) {
				chest.getAnimator().ensureRunning("Open", KeyBoard.isKeyDown(GLFW.GLFW_KEY_E), false);
				chest.getAnimator().ensureRunning("Open", false, false);
				timePassed += deltaTime;
			} else {
				if (!alreadyOpened) {
					consumer.accept(chest);
				}
				alreadyOpened = true;
			}
		}
		if (alreadyOpened) {
			chest.getAnimator().ensureRunning("Opened");
		}
	}

}
