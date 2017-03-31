package mbeb.opengldefault.scene.behaviour;

import org.joml.Vector2f;
import org.joml.Vector3f;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.scene.entities.Entity;
import mbeb.opengldefault.scene.entities.IEntity;

import java.lang.Math;

/**
 * A Behaviour that translates Mouse input to a pitch and yaw value to set as the direction of an Entity
 *
 * @author Markus
 */
public class PitchYawMouseBehaviour implements IBehaviour {

	/** mouse position at last update time */
	private Vector2f lastMousePosition;
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
		lastMousePosition = new Vector2f(Mouse.getPos());
	}

	@Override
	public boolean triggers(IEntity entity) {
		return true;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {

		final Vector2f delta = lastMousePosition.sub(Mouse.getPos(), new Vector2f());

		lastMousePosition = new Vector2f(Mouse.getPos());

		/*if (!Mouse.isDown(GLFW_MOUSE_BUTTON_1)) {
			return;
		}*/

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
	private void updateDirection(IEntity entity) {
		Vector3f direction = new Vector3f();

		direction.x = (float) (Math.cos(pitch) * Math.cos(yaw));
		direction.y = (float) Math.sin(pitch);
		direction.z = (float) (Math.cos(pitch) * Math.sin(yaw));

		direction.normalize();

		entity.setDirection(direction);
	}

}
