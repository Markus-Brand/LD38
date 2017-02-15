package mbeb.opengldefault.camera;

import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.openglcontext.*;

import org.joml.*;
import org.lwjgl.glfw.*;

public class FirstPersonCamera extends Camera {

	/** Class Name Tag */
	private static final String TAG = "FirstPersonCamera";
	/** (0,1,0) */
	private final Vector3f worldUp;
	/** mouse position at last update time */
	private Vector2f lastMousePos;
	/** current orientation */
	private float pitch, yaw;

	private final float movementSpeed, rotationSpeed;

	private float distanceTravelled;

	private final float viewBobbingAltitude;

	private final float viewBobbingDistance;

	/**
	 * @param position
	 * @param direction
	 */
	public FirstPersonCamera(final Vector3f position, final Vector3f direction) {
		lastMousePos = new Vector2f(Mouse.getPos());
		projection = new Matrix4f();
		projection.perspective((float) java.lang.Math.PI / 2.5f,
				OpenGLContext.getWidth() / (float) OpenGLContext.getHeight(), 0.1f, 100);

		this.position = position;
		this.viewDirection = direction;
		this.worldUp = new Vector3f(0, 1, 0);

		movementSpeed = 0.1f;
		rotationSpeed = 0.01f;

		viewBobbingAltitude = 0.0f;

		viewBobbingDistance = 0.2f;

		updateView();
	}

	private void updateView() {

		final float viewBobbing =
				(float) (java.lang.Math.sin(distanceTravelled / viewBobbingDistance) * viewBobbingAltitude);
		final Vector3f cameraPos = new Vector3f(position);
		cameraPos.y += viewBobbing;

		final Vector3f center = new Vector3f();
		view = new Matrix4f();
		cameraPos.add(viewDirection, center);

		view.lookAt(cameraPos, center, worldUp);
		projectionView = null;
		updateUniformBlock();
	}

	@Override
	public void setViewDirection(final Vector3f direction) {
		direction.normalize(this.viewDirection);
		updateView();
	}

	@Override
	public void setPosition(final Vector3f position) {
		super.setPosition(position);
		updateView();
	}

	@Override
	public void update(final double deltaTime) {
		updateDirection();
		updatePosition();
		updateView();
	}

	private void updatePosition() {
		final Vector3f delta = new Vector3f();
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
		delta.mul(movementSpeed);
		position.add(delta);

		distanceTravelled += delta.length();
	}

	private void updateDirection() {
		final Vector2f delta = new Vector2f();

		lastMousePos.sub(Mouse.getPos(), delta);

		lastMousePos = new Vector2f(Mouse.getPos());

		delta.mul(rotationSpeed);

		pitch += delta.y;
		yaw += -delta.x;

		if (pitch >= 0.45f * java.lang.Math.PI) {
			pitch = 0.45f * (float) java.lang.Math.PI;
		}
		if (pitch <= -0.45f * java.lang.Math.PI) {
			pitch = -0.45f * (float) java.lang.Math.PI;
		}

		viewDirection.x = (float) (java.lang.Math.cos(pitch) * java.lang.Math.cos(yaw));
		viewDirection.y = (float) java.lang.Math.sin(pitch);
		viewDirection.z = (float) (java.lang.Math.cos(pitch) * java.lang.Math.sin(yaw));

		viewDirection.normalize();
	}
}
