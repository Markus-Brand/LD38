package mbeb.opengldefault.camera;

import org.joml.Matrix4f;

public interface ICamera {

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
