package mbeb.opengldefault.scene.behaviour;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.scene.entities.IEntity;

public class WalkOnHeightMapBehaviour extends PitchYawMouseBehaviour {

	private static final Vector3f worldUp = new Vector3f(0, 1, 0);
	private HeightSource heightSource;
	private float speed;

	public WalkOnHeightMapBehaviour(HeightSource heightSource, float speed) {
		this.heightSource = heightSource;
		this.speed = speed;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		super.update(deltaTime, entity);

		Vector3f position = entity.getPosition();
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
			//return;
		}

		Vector3f newPosition = delta.mul(speed * (float) deltaTime).add(position);

		float newHeight = heightSource.getHeight(new Vector2f(newPosition.x, newPosition.z));

		if (newHeight < 3f) {
			entity.setPosition(new Vector3f(newPosition.x, newHeight, newPosition.z));
		}

	}

	@Override
	protected void updateDirection(IEntity entity) {
		Vector3f direction = new Vector3f();

		direction.x = (float) Math.cos(yaw / 3);
		direction.y = 0;
		direction.z = (float) Math.sin(yaw / 3);

		direction.normalize();

		entity.setDirection(direction);
	}

}
