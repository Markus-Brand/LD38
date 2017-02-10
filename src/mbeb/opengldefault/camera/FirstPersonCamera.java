package mbeb.opengldefault.camera;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.openglcontext.OpenGLContext;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class FirstPersonCamera extends Camera {

	/** Class Name Tag */
	private static final String TAG = "FirstPersonCamera";

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
		projection.perspective((float) Math.PI / 2, OpenGLContext.getWidth() / (float) OpenGLContext.getHeight(), 0.1f, 100);

		this.position = position;
		this.viewDirection = direction;
		this.worldUp = new Vector3f(0, 1, 0);

		speed = 0.1f;
		rotationSpeed = 0.01f;

		viewBobbingAltitude = 0.0f;

		viewBobbingDistance = 0.2f;

		updateView();
	}

	private void updateView() {

		float viewBobbing = (float) (Math.sin(distanceTravelled / viewBobbingDistance) * viewBobbingAltitude);
		Vector3f cameraPos = new Vector3f(position);
		cameraPos.y += viewBobbing;

		Vector3f center = new Vector3f();
		view = new Matrix4f();
		cameraPos.add(viewDirection, center);

		view.lookAt(cameraPos, center, worldUp);
		projectionView = null;
		updateUniformBlock();
	}

	public void setDirection(Vector3f direction) {
		direction.normalize(this.viewDirection);
		updateView();
	}

	@Override
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
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_A)) {
			worldUp.cross(viewDirection, delta);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_D)) {
			viewDirection.cross(worldUp, delta);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_W)) {
			delta.add(viewDirection);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_S)) {
			delta.sub(viewDirection);
		}
		if (delta.length() == 0) {
			return;
		}
		delta.normalize();
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

		viewDirection.x = (float) (Math.cos(pitch) * Math.cos(yaw));
		viewDirection.y = (float) Math.sin(pitch);
		viewDirection.z = (float) (Math.cos(pitch) * Math.sin(yaw));

		viewDirection.normalize();
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

}
