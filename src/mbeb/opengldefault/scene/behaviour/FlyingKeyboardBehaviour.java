package mbeb.opengldefault.scene.behaviour;

import org.joml.*;
import org.lwjgl.glfw.*;

import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.scene.entities.*;

/**
 * A Behaviour that makes a Entity fly along its viewDirection by using KeyBoard Input
 * 
 * @author Markus
 */
public class FlyingKeyboardBehaviour implements IBehaviour {
	/** speed of the movement */
	private final float movementSpeed;
	/** (0,1,0) */
	private final Vector3f worldUp;

	public FlyingKeyboardBehaviour() {
		this(0.1f);
	}

	public FlyingKeyboardBehaviour(float movementSpeed) {
		this.movementSpeed = movementSpeed;
		this.worldUp = new Vector3f(0, 1, 0);
	}

	@Override
	public boolean triggers(Entity entity) {
		return true;
	}

	@Override
	public void update(double deltaTime, Entity entity) {
		Vector3f direction = entity.getDirection();

		final Vector3f delta = new Vector3f();

		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_A)) {
			worldUp.cross(direction, delta);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_D)) {
			direction.cross(worldUp, delta);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_W)) {
			delta.add(direction);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_S)) {
			delta.sub(direction);
		}
		if (delta.length() == 0) {
			return;
		}
		delta.normalize();
		delta.mul(movementSpeed);

		entity.setPosition(entity.getPosition().add(delta, new Vector3f()));
	}
}
