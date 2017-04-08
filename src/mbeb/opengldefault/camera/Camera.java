package mbeb.opengldefault.camera;

import static org.lwjgl.opengl.GL15.*;

import java.nio.*;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gl.buffer.UniformBuffer;
import org.joml.*;
import org.lwjgl.*;

import mbeb.opengldefault.rendering.shader.*;

public class Camera implements ICamera {

	public static final float FOV_ANGLE = 70;
	public static final float FOV = (float) java.lang.Math.toRadians(FOV_ANGLE);
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;

	/** Class Name Tag */
	private static final String TAG = "Camera";

	/** Cameras View Matrix */
	protected Matrix4f view;

	/** Cameras projection Matrix */
	protected Matrix4f projection;

	/** Cameras projectionView Matrix */
	protected Matrix4f projectionView;

	/** Uniform Buffer containing the Matrix data */
	private UniformBuffer UBO = UBOManager.MATRICES;

	/** Position of the Camera */
	protected Vector3f position;

	/** View Direction of the Camera */
	protected Vector3f viewDirection;

	/**
	 * Basic Camera Constructor. Sets the projection to a default perspective
	 * projection and the view to Camera looking from origin along positive z
	 * direction.
	 *
	 * @param aspectRation
	 *            the aspect ratio of the camera
	 */
	public Camera(final float aspectRation) {
		projection = new Matrix4f();
		view = new Matrix4f();
		projectionView = null;
		projection.perspective(FOV, aspectRation, NEAR_PLANE, FAR_PLANE);

		view.lookAlong(new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));

		projection.mul(view, getProjectionView());
		position = new Vector3f();
		viewDirection = new Vector3f(1, 0, 0);


		UBO.bind();
		UBO.bufferData(256, GL_DYNAMIC_DRAW);
		UBO.bindBufferBase();
		UBO.unbind();
		updateUniformBlock();
	}

	@Override
	public void update(final double deltaTime) {
		updateView();
	}

	private void updateView() {
		final Matrix4f newView = new Matrix4f();

		final Vector3f lookCenter = getPosition().add(getViewDirection(), new Vector3f());
		newView.lookAt(getPosition(), lookCenter, new Vector3f(0, 1, 0));

		setView(newView);

		updateUniformBlock();
	}

	@Override
	public Matrix4f getView() {
		return view;
	}

	@Override
	public void setView(final Matrix4f view) {
		this.view = view;
		projectionView = null;
		updateUniformBlock();
	}

	@Override
	public Matrix4f getProjection() {
		return projection;
	}

	@Override
	public void setProjection(final Matrix4f projection) {
		this.projection = projection;
		updateUniformBlock();
	}

	@Override
	public Matrix4f getProjectionView() {
		if (projectionView == null) {
			projectionView = new Matrix4f();
			getProjection().mul(getView(), projectionView);
		}
		return projectionView;
	}

	@Override
	public Matrix4f getSkyboxView() {
		return new Matrix4f(new Matrix3f(getView()));
	}

	@Override
	public void updateUniformBlock() {
		UBO.writer(Constants.MAT4_SIZE * 4)
				.write(getProjection())
				.write(getView())
				.write(getProjectionView())
				.write(getSkyboxView())
				.flush();
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public void setPosition(final Vector3f position) {
		this.position = position;
	}

	@Override
	public Vector3f getViewDirection() {
		return viewDirection;
	}

	@Override
	public void setViewDirection(final Vector3f newViewDirection) {
		this.viewDirection = newViewDirection;
	}

	@Override
	public Vector3f getPosOnScreen(final Vector3f pos) {
		return getPosOnScreen(new Vector4f(pos.x, pos.y, pos.z, 1));
	}

	@Override
	public Vector3f getPosOnScreen(final Vector4f pos) {
		final Vector4f res = pos.mul(getProjectionView());
		return new Vector3f(res.x / res.w, res.y / res.w, res.z / res.w);
	}
}
