package mbeb.opengldefault.camera;

import org.joml.Matrix4f;

import mbeb.opengldefault.gl.GLContext;

/**
 * Represents a camera with a perspective projection matrix.
 */
public class PerspectiveCamera extends Camera {

	private static final float DEFAULT_FOV = (float) Math.toRadians(70.0);

	/**
	 * The vertical field of view of this camera.
	 * The unit is radians.
	 */
	private float fov;

	/**
	 * Creates a new perspective camera with the contexts aspect ratio and the default field of view.
	 */
	public PerspectiveCamera() {
		this(GLContext.getAspectRatio());
	}

	/**
	 * Creates a new perspective camera with the given aspect ratio and default field of view, near and far.
	 * 
	 * @param aspectRatio
	 *            the aspect ratio
	 */
	public PerspectiveCamera(final float aspectRatio) {
		this(DEFAULT_FOV, aspectRatio);
	}

	/**
	 * Creates a new perspective camera with the given aspect ratio and field of view, with default near and far.
	 * 
	 * @param fov
	 *            the field of view
	 * @param aspectRatio
	 *            the aspect ratio
	 */
	public PerspectiveCamera(final float fov, final float aspectRatio) {
		this(fov, aspectRatio, DEFAULT_NEAR, DEFAULT_FAR);
	}

	/**
	 * Creates a new perspective camera with the given parameters.
	 * 
	 * @param fov
	 *            the field of view
	 * @param aspectRatio
	 *            the aspect ratio
	 * @param near
	 *            the near clipping plane
	 * @param far
	 *            the far clipping plane
	 */
	public PerspectiveCamera(final float fov, final float aspectRatio, final float near, final float far) {
		super();
		this.setFOV(fov);
		this.setAspectRatio(aspectRatio);
		this.setNear(near);
		this.setFar(far);
	}

	@Override
	protected Matrix4f generateProjection() {
		return new Matrix4f().perspective(this.getFOV(), this.getAspectRatio(), this.getNear(), this.getFar());
	}

	/**
	 * @return the vertical field of view, in radians
	 */
	public float getFOV() {
		return fov;
	}

	/**
	 * Sets the vertical field of view of this camera.
	 * 
	 * @param fov
	 *            the field of view, in radians
	 */
	public void setFOV(final float fov) {
		this.fov = fov;
		this.setProjectionDirty();
	}
}
