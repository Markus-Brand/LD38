package mbeb.opengldefault.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

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
	 * @param newPosition
	 *            the new camera position
	 */
	void setPosition(Vector3f newPosition);

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
	 * get UBO that stores the view and projection as well as a viewProjection matrix
	 *
	 * @return the UBO
	 */
	int getUBO();

	/**
	 * updates the UBO: Buffers the view, projection and viewProjection matrix into the UBO
	 */
	void updateUniformBlock();
}
