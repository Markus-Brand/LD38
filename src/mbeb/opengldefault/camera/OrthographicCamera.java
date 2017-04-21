package mbeb.opengldefault.camera;

import org.joml.Matrix4f;

import mbeb.opengldefault.gl.GLContext;

/**
 * Represents a camera with an orthographic projection matrix
 */
public class OrthographicCamera extends Camera {

	/**
	 * The orthographic scale of this camera.
	 */
	private float scale;

	/**
	 * Creates a new orthographic camera with the given orthographic scale and the contexts aspect ratio.
	 * 
	 * @param scale
	 *            the orthographic scale of this camera
	 */
	public OrthographicCamera(final float scale) {
		this(scale, GLContext.getAspectRatio());
	}

	/**
	 * Creates a new orthographic camera with the given orthographic scale and aspect ratio.
	 * 
	 * @param scale
	 *            the orthographic scale of this camera
	 * @param aspectRatio
	 *            the aspect ratio for this camera
	 */
	public OrthographicCamera(final float scale, final float aspectRatio) {
		this(scale, aspectRatio, DEFAULT_NEAR, DEFAULT_FAR);
	}

	public OrthographicCamera(final float scale, final float aspectRatio, final float near, final float far) {
		super();
		this.setScale(scale);
		this.setAspectRatio(aspectRatio);
		this.setNear(near);
		this.setFar(far);
	}

	@Override
	protected Matrix4f generateProjection() {
		float top = this.getScale() / 2.0f;
		float bottom = -top;
		float right = (this.getScale() * this.getAspectRatio()) / 2.0f;
		float left = -right;
		return new Matrix4f().ortho(left, right, bottom, top, this.getNear(), this.getFar());
	}

	/**
	 * @return the top clipping plane of this camera
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Sets the orthographic scale of this camera.
	 *
	 * @param scale
	 *            the orthographic scale
	 */
	public void setScale(final float scale) {
		this.scale = scale;
		this.setProjectionDirty();
	}
}
