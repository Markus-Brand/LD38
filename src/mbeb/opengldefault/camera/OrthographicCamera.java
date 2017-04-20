package mbeb.opengldefault.camera;

import org.joml.Matrix4f;

/**
 * Represents a camera with an orthographic projection matrix
 */
public class OrthographicCamera extends Camera {

	/**
	 * The top clipping plane of this camera.
	 */
	private float top;

	/**
	 * The bottom clipping plane of this camera.
	 */
	private float bottom;

	/**
	 * The left clipping plane of this camera.
	 */
	private float left;

	/**
	 * The right clipping plane of this camera.
	 */
	private float right;

	/**
	 * Creates a new orthographic camera with the given clipping planes and default near and far.
	 * 
	 * @param top
	 *            the top plane
	 * @param bottom
	 *            the bottom plane
	 * @param left
	 *            the left plane
	 * @param right
	 *            the right plane
	 */
	public OrthographicCamera(final float top, final float bottom, final float left, final float right) {
		this(top, bottom, left, right, DEFAULT_NEAR, DEFAULT_FAR);
	}

	/**
	 * Creates a new orthographic camera with the given clipping planes.
	 * 
	 * @param top
	 *            the top plane
	 * @param bottom
	 *            the bottom plane
	 * @param left
	 *            the left plane
	 * @param right
	 *            the right plane
	 * @param near
	 *            the near plane
	 * @param far
	 *            the far plane
	 */
	public OrthographicCamera(final float top, final float bottom, final float left, final float right, final float near, final float far) {
		super();
		this.setTop(top);
		this.setBottom(bottom);
		this.setLeft(left);
		this.setRight(right);
		this.setNear(near);
		this.setFar(far);
	}

	@Override
	protected Matrix4f generateProjection() {
		return new Matrix4f().ortho(this.getLeft(), this.getRight(), this.getBottom(), this.getTop(), this.getNear(), this.getFar());
	}

	/**
	 * @return the top clipping plane of this camera
	 */
	public float getTop() {
		return top;
	}

	/**
	 * Sets the top clipping plane of this camera.
	 * 
	 * @param top
	 *            the top plane
	 */
	public void setTop(final float top) {
		this.top = top;
		this.setProjectionDirty();
	}

	/**
	 * @return the bottom clipping plane of this camera
	 */
	public float getBottom() {
		return bottom;
	}

	/**
	 * Sets the bottom clipping plane of this camera.
	 * 
	 * @param bottom
	 *            the bottom plane
	 */
	public void setBottom(final float bottom) {
		this.bottom = bottom;
		this.setProjectionDirty();
	}

	/**
	 * @return the left clipping plane of this camera
	 */
	public float getLeft() {
		return left;
	}

	/**
	 * Sets the left clipping plane of this camera.
	 * 
	 * @param left
	 *            the left plane
	 */
	public void setLeft(final float left) {
		this.left = left;
		this.setProjectionDirty();
	}

	/**
	 * @return the right clipping plane of this camera
	 */
	public float getRight() {
		return right;
	}

	/**
	 * Sets the right clipping plane of this camera.
	 * 
	 * @param right
	 *            the right plane
	 */
	public void setRight(final float right) {
		this.right = right;
		this.setProjectionDirty();
	}
}
