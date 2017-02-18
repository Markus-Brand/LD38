package mbeb.opengldefault.camera;

import org.joml.*;

public interface ICamera {

	/**
	 * updates the camera
	 *
	 * @param deltaTime
	 *            time, that passed since the last update
	 */
	default void update(double deltaTime) {
		updateView();
	}

	/**
	 * get the cameras position
	 *
	 * @return current position of the camera
	 */
	Vector3f getPosition();

	/**
	 * sets a new camera position
	 *
	 * @param position
	 *            the new camera position
	 */
	void setPosition(Vector3f position);

	/**
	 * get the cameras view direction
	 *
	 * @return current view direction of the camera
	 */
	Vector3f getViewDirection();

	/**
	 * sets a new camera view direction
	 *
	 * @param newViewDirection
	 *            the new camera view direction
	 */
	void setViewDirection(Vector3f newViewDirection);

	/**
	 * get the view matrix
	 *
	 * @return view matrix
	 */
	Matrix4f getView();

	/**
	 * set view matrix
	 *
	 * @param view
	 *            new view matrix
	 */
	void setView(Matrix4f view);

	/**
	 * get projection matrix
	 *
	 * @return projection matrix
	 */
	Matrix4f getProjection();

	/**
	 * set projection matrix
	 *
	 * @param projection
	 *            new projection matrix
	 */
	void setProjection(Matrix4f projection);

	/**
	 * get projectionView matrix. This is mostly just projection * view.
	 *
	 * @return projectionView matrix
	 */
	Matrix4f getProjectionView();

	/**
	 * get skybox view matrix. Should be something like mat4(mat3(view))
	 *
	 * @return skybox view matrix
	 */
	Matrix4f getSkyboxView();

	/**
	 * get UBO that stores the view and projection as well as a viewProjection matrix
	 *
	 * @return the UBO
	 */
	int getUBO();

	/**
	 * updates the UBO: Buffers the view, projection and viewProjection matrix into the UBO
	 */
	void updateUniformBlock();

	default void updateView() {
		Matrix4f view = new Matrix4f();

		view.lookAt(getPosition(), getPosition().add(getViewDirection(), new Vector3f()), new Vector3f(0, 1, 0));

		setView(view);

		updateUniformBlock();
	}

	/**
	 * convert the given world space coordinates to screen space
	 *
	 * @param pos
	 * @return
	 */
	default Vector3f getPosOnScreen(Vector3f pos) {
		return getPosOnScreen(new Vector4f(pos.x, pos.y, pos.z, 1));
	}

	/**
	 * convert the given world space coordinates to screen space
	 *
	 * @param pos
	 * @return
	 */
	default Vector3f getPosOnScreen(Vector4f pos) {
		Vector4f res = pos.mul(getProjectionView());
		return new Vector3f(res.x / res.z, res.y / res.z, res.z);
	}
}
