package camera;

import main.KeyBoard;
import main.Main;
import main.Mouse;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class FirstPersonCamera extends Camera {

	private Vector3f position;
	private Vector3f direction;
	private Vector3f worldUp;

	private Vector2f lastMousePos;

	private float pitch, yaw;

	private float speed, rotationSpeed;

	private float distanceTravelled;

	private float viewBobbingAltitude;

	private float viewBobbingDistance;

	public FirstPersonCamera(Vector3f position, Vector3f direction) {
		lastMousePos = new Vector2f(Mouse.getPos());
		projection = new Matrix4f();
		projection.perspective((float) Math.PI / 2, Main.getWidth() / (float) Main.getHeight(), 0.1f, 100);

		this.position = position;
		this.direction = direction;
		this.worldUp = new Vector3f(0, 1, 0);

		speed = 0.1f;
		rotationSpeed = 0.01f;

		viewBobbingAltitude = 0.3f;

		viewBobbingDistance = 1f;

		updateView();
	}

	private void updateView() {

		float viewBobbing = (float) (Math.sin(distanceTravelled / viewBobbingDistance) * viewBobbingAltitude);
		Vector3f cameraPos = new Vector3f(position);
		cameraPos.y += viewBobbing;

		Vector3f center = new Vector3f();
		view = new Matrix4f();
		cameraPos.add(direction, center);

		view.lookAt(cameraPos, center, worldUp);
		projectionView = null;
		updateUniformBlock();
	}

	public void setDirection(Vector3f direction) {
		direction.normalize(this.direction);
		updateView();
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		updateView();
	}

	public void update() {
		updateDirection();
		updatePosition();
		updateView();
	}

	private void updatePosition() {
		Vector3f delta = new Vector3f();
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_W)) {
			delta.add(direction);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_S)) {
			delta.sub(direction);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_A)) {
			worldUp.cross(direction, delta);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_D)) {
			direction.cross(worldUp, delta);
		}
		delta.mul(speed);
		position.add(delta);

		distanceTravelled += delta.length();
	}

	private void updateDirection() {
		Vector2f delta = new Vector2f();

		lastMousePos.sub(Mouse.getPos(), delta);

		lastMousePos = new Vector2f(Mouse.getPos());

		delta.mul(rotationSpeed);

		pitch += delta.y;
		yaw += -delta.x;

		if (pitch >= 0.45f * Math.PI) {
			pitch = 0.45f * (float) Math.PI;
		}
		if (pitch <= -0.45f * Math.PI) {
			pitch = -0.45f * (float) Math.PI;
		}

		direction.x = (float) (Math.cos(pitch) * Math.cos(yaw));
		direction.y = (float) Math.sin(pitch);
		direction.z = (float) (Math.cos(pitch) * Math.sin(yaw));

		direction.normalize();
	}
}
