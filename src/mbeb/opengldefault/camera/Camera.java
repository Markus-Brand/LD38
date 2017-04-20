package mbeb.opengldefault.camera;

import static mbeb.opengldefault.constants.Constants.MAT4_SIZE;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import mbeb.opengldefault.gl.buffer.GLBuffer;
import mbeb.opengldefault.gl.buffer.UniformBuffer;
import mbeb.opengldefault.scene.entities.CameraEntity;
import mbeb.opengldefault.scene.entities.IEntityConvertable;

public abstract class Camera implements IEntityConvertable {

	private static final String TAG = "Camera";

	protected static final float DEFAULT_NEAR = 0.1f;
	protected static final float DEFAULT_FAR = 1000f;

	/**
	 * The uniform buffer index for camera matrices.
	 */
	public static final int UBO_INDEX = 0;

	/**
	 * The uniform buffer name for camera matrices.
	 */
	public static final String UBO_NAME = "Matrices";

	/**
	 * The position of this camera in world space.
	 * Defaults to (0,0,0).
	 */
	protected Vector3f eye;

	/**
	 * The up vector of this camera.
	 * Defaults to (0,1,0).
	 */
	protected Vector3f up;

	/**
	 * The position this camera is looking at, in world space.
	 * Defaults to (1,0,0).
	 */
	protected Vector3f center;

	/**
	 * Whether the parameters relevant to the view matrix have been changed.
	 */
	private boolean viewDirty;

	/**
	 * Whether the parameters relevant to the projection matrix have been changed.
	 */
	private boolean projectionDirty;

	/**
	 * Whether the view-projection matrix requires an update.
	 */
	private boolean projectionViewDirty;

	/**
	 * Whether the parameters relevant to the view matrix have been changed.
	 */
	private boolean uboDirty;

	/**
	 * The near clipping plane of this camera.
	 */
	private float near;

	/**
	 * The far clipping plane of this camera.
	 */
	private float far;

	/**
	 * The aspect ratio of this camera (width / height).
	 */
	private float aspectRatio;

	/**
	 * The cached view matrix of this camera.
	 */
	private Matrix4f view;

	/**
	 * The cached projection matrix of this camera.
	 */
	private Matrix4f projection;

	/**
	 * The cached view-projection matrix of this camera.
	 */
	private Matrix4f projectionView;

	/**
	 * The uniform buffer object of this texture.
	 */
	private UniformBuffer ubo;

	/**
	 * Creates a new camera an initializes its UBO.
	 */
	protected Camera() {
		this.ubo = new UniformBuffer(UBO_INDEX, UBO_NAME, MAT4_SIZE * 3);
		ubo.whileBound(glObject -> {
			ubo.bufferData(MAT4_SIZE * 3, GLBuffer.Usage.DYNAMIC_DRAW);
			return true;
		});
		this.setUBODirty();
		this.setEye(new Vector3f());
		this.setCenter(new Vector3f(1, 0, 0));
		this.setUp(new Vector3f(0, 1, 0));
	}

	/**
	 * Performs any time dependent updates of this camera.
	 * 
	 * @param deltaTime
	 *            the time that has passed
	 */
	public void update(final double deltaTime) {
	}

	/**
	 * @return whether the UBO requires an update
	 */
	protected boolean requiresUBOUpdate() {
		return this.isUBODirty() || this.isViewDirty() || this.isProjectionDirty();
	}

	/**
	 * @return whether the UBO data has been marked as dirty
	 */
	protected boolean isUBODirty() {
		return this.uboDirty;
	}

	/**
	 * Marks the UBO data as dirty.
	 */
	protected void setUBODirty() {
		this.uboDirty = true;
	}

	/**
	 * Marks the UBO data as clean.
	 */
	protected void setUBOClean() {
		this.uboDirty = false;
	}

	/**
	 * Updates the cameras UBO as dirty.
	 */
	public final void updateUniformBlock() {
		if (this.requiresUBOUpdate()) {
			ubo.writer().write(this.getProjection()).write(this.getView()).write(this.getProjectionView()).flush();
			this.setUBOClean();
		}
	}

	/**
	 * @return whether the view matrix needs to be updated
	 */
	protected boolean isViewDirty() {
		return this.viewDirty;
	}

	/**
	 * Marks the view data as dirty.
	 */
	protected void setViewDirty() {
		this.viewDirty = true;
	}

	/**
	 * Marks the view data as clean.
	 */
	protected void setViewClean() {
		this.viewDirty = false;
	}

	/**
	 * Generates a new view matrix for this camera.
	 * 
	 * @return the generated matrix
	 */
	protected Matrix4f generateView() {
		return new Matrix4f().lookAt(this.getEye(), this.getCenter(), this.getUp());
	}

	/**
	 * @return an up-to-date view matrix for this camera
	 */
	public final Matrix4f getView() {
		if (this.isViewDirty()) {
			this.view = generateView();
			this.setViewClean();
			this.setProjectionViewDirty();
			this.setUBODirty();
		}
		return this.view;
	}

	/**
	 * @return whether the projection matrix requires an update
	 */
	protected boolean isProjectionDirty() {
		return this.projectionDirty;
	}

	/**
	 * Marks the projection data as dirty.
	 */
	protected void setProjectionDirty() {
		this.projectionDirty = true;
	}

	/**
	 * Marks the projection data as clean.
	 */
	protected void setProjectionClean() {
		this.projectionDirty = false;
	}

	/**
	 * @return whether the view-projection matrix requires an update
	 */
	protected boolean isProjectionViewDirty() {
		return this.projectionViewDirty;
	}

	/**
	 * Marks the view-projection data as dirty.
	 */
	protected void setProjectionViewDirty() {
		this.projectionViewDirty = true;
	}

	/**
	 * Marks the view-projection data as clean.
	 */
	protected void setProjectionViewClean() {
		this.projectionViewDirty = false;
	}

	/**
	 * Generates a new projection matrix for this camera.
	 * 
	 * @return the generated matrix
	 */
	protected abstract Matrix4f generateProjection();

	/**
	 * @return an up-to-date projection matrix for this camera
	 */
	public final Matrix4f getProjection() {
		if (this.isProjectionDirty()) {
			this.projection = this.generateProjection();
			this.setProjectionClean();
			this.setProjectionViewDirty();
			this.setUBODirty();
		}
		return this.projection;
	}

	/**
	 * @return an up-to-date projection-view matrix for this camera
	 */
	public final Matrix4f getProjectionView() {
		if (this.isProjectionViewDirty()) {
			this.projectionView = this.getProjection().mul(this.getView(), new Matrix4f());
			this.setProjectionViewClean();
			this.setUBODirty();
		}
		return this.projectionView;
	}

	/**
	 * @return the eye coordinates of this camera
	 */
	public Vector3f getEye() {
		return eye;
	}

	/**
	 * Sets the eye coordinates of this camera.
	 * 
	 * @param eye
	 *            the new eye coordinates
	 */
	public void setEye(final Vector3f eye) {
		this.eye = eye;
		this.setViewDirty();
	}

	/**
	 * @return the up vector of this camera
	 */
	public Vector3f getUp() {
		return up;
	}

	/**
	 * Sets the up vector of this camera.
	 * 
	 * @param up
	 *            the new up vector
	 */
	public void setUp(final Vector3f up) {
		this.up = up;
		this.setViewDirty();
	}

	/**
	 * @return the center position of this camera
	 */
	public Vector3f getCenter() {
		return center;
	}

	/**
	 * Sets the center position of this camera.
	 * 
	 * @param center
	 *            the new center position
	 */
	public void setCenter(final Vector3f center) {
		this.center = center;
		this.setViewDirty();
	}

	/**
	 * @return the near plane of this camera
	 */
	public float getNear() {
		return near;
	}

	/**
	 * Sets the near plane of this camera.
	 * 
	 * @param near
	 *            the near plane
	 */
	public void setNear(final float near) {
		this.near = near;
		this.setProjectionDirty();
	}

	/**
	 * @return the far plane of this camera
	 */
	public float getFar() {
		return far;
	}

	/**
	 * Sets the far plane of this camera.
	 * 
	 * @param far
	 *            the far plane
	 */
	public void setFar(final float far) {
		this.far = far;
		this.setProjectionDirty();
	}

	/**
	 * @return the aspect ratio of this camera (width / height)
	 */
	public float getAspectRatio() {
		return aspectRatio;
	}

	/**
	 * Sets the aspect ratio of this camera.
	 *
	 * @param aspectRatio
	 *            the aspect ratio (width / height)
	 */
	public void setAspectRatio(final float aspectRatio) {
		this.aspectRatio = aspectRatio;
		this.setProjectionDirty();
	}

	/**
	 * Sets this camera as the active camera.
	 */
	public void use() {
		this.updateUniformBlock();
		this.ubo.bindBufferBase();
	}

	/**
	 * Gets the view transformation suitable for skybox use (without translation).
	 * 
	 * @return the skybox view matrix
	 */
	public Matrix4f getSkyboxView() {
		return new Matrix4f(new Matrix3f(getView()));
	}

	/**
	 * @param pos
	 *            the 3D global space coordinates
	 * @return the screen space coordinates for the position
	 */
	public Vector3f getPositionOnScreen(final Vector3f pos) {
		return getPositionOnScreen(new Vector4f(pos.x, pos.y, pos.z, 1));
	}

	/**
	 * @param pos
	 *            the homogenous 4D vector in global space
	 * @return the screen space coordinates for the position
	 */
	public Vector3f getPositionOnScreen(final Vector4f pos) {
		final Vector4f res = pos.mul(getProjectionView());
		return new Vector3f(res.x / res.w, res.y / res.w, res.z / res.w);
	}

	@Override
	public CameraEntity asNewEntity() {
		return new CameraEntity(this);
	}
}
