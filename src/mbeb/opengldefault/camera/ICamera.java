package mbeb.opengldefault.camera;

import org.joml.*;

public interface ICamera {

	/**
	 * updates the camera
	 *
	 * @param deltaTime
	 *            time, that passed since the last update
	 */
	void update(double deltaTime);

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
	 * updates the UBO: Buffers the view, projection and viewProjection matrix into the UBO
	 */
	void updateUniformBlock();

	/**
	 * convert the given world space coordinates to screen space
	 *
	 * @param pos
	 * @return the Vectors position on Screen
	 */
	Vector3f getPosOnScreen(Vector3f pos);

	/**
	 * convert the given world space coordinates to screen space
	 *
	 * @param pos
	 * @return the Vectors position on Screen
	 */
	Vector3f getPosOnScreen(Vector4f pos);
}
