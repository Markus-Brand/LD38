package mbeb.opengldefault.scene.behaviour;

import static org.lwjgl.glfw.GLFW.*;

import java.lang.Math;

import org.joml.*;

import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.scene.entities.*;

/**
 * A Behaviour that translates Mouse input to a pitch and yaw value to set as the direction of an Entity
 *
 * @author Markus
 */
public class PitchYawMouseBehaviour implements IBehaviour {

	/** mouse position at last update time */
	private Vector2f lastMousePos;
	/** current orientation */
	private float pitch, yaw;
	/** speed of the rotation in radians per Pixel */
	private final float rotationSpeed;

	public PitchYawMouseBehaviour() {
		this(0, 0, 0.01f);
	}

	public PitchYawMouseBehaviour(float pitch, float yaw, float rotationSpeed) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.rotationSpeed = rotationSpeed;
		lastMousePos = new Vector2f(Mouse.getPos());
	}

	@Override
	public boolean triggers(Entity entity) {
		return true;
	}

	@Override
	public void update(double deltaTime, Entity entity) {

		final Vector2f delta = lastMousePos.sub(Mouse.getPos(), new Vector2f());

		lastMousePos = new Vector2f(Mouse.getPos());

		if (!Mouse.isDown(GLFW_MOUSE_BUTTON_1)) {
			return;
		}

		delta.mul(rotationSpeed);

		pitch += delta.y;
		yaw -= delta.x;

		if (pitch >= 0.45f * java.lang.Math.PI) {
			pitch = 0.45f * (float) java.lang.Math.PI;
		}
		if (pitch <= -0.45f * java.lang.Math.PI) {
			pitch = -0.45f * (float) java.lang.Math.PI;
		}

		updateDirection(entity);
	}

	/**
	 * calculates the direction based on the pitch and yaw value and sets the entitiies direction
	 *
	 * @param entity
	 *            the entity the behaviour is applied to
	 */
	private void updateDirection(Entity entity) {
		Vector3f direction = new Vector3f();

		direction.x = (float) (Math.cos(pitch) * Math.cos(yaw));
		direction.y = (float) Math.sin(pitch);
		direction.z = (float) (Math.cos(pitch) * Math.sin(yaw));

		direction.normalize();

		entity.setDirection(direction);
	}

}
