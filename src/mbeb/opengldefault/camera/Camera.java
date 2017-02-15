package mbeb.opengldefault.camera;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.openglcontext.*;

import org.joml.*;
import org.lwjgl.*;

public abstract class Camera implements ICamera {

	/**
	 * Class Name Tag
	 */
	private static final String TAG = "Camera";

	/** Cameras View Matrix */
	protected Matrix4f view;

	/** Cameras projection Matrix */
	protected Matrix4f projection;

	/** Cameras projectionView Matrix */
	protected Matrix4f projectionView;

	/** Uniform Buffer containing the Matrix data */
	private int UBO = -1;

	/** Position of the Camera */
	protected Vector3f position;

	/** View Direction of the Camera */
	protected Vector3f viewDirection;

	/**
	 * Basic Camera Constructor. Sets the projection to a default perspective
	 * projection and the view to Camera looking from origin along positive z
	 * direction.
	 */
	public Camera() {
		projection = new Matrix4f();
		view = new Matrix4f();
		projectionView = new Matrix4f();
		projection.perspective((float) (java.lang.Math.PI / 2),
				OpenGLContext.getWidth() / (float) OpenGLContext.getHeight(), 0.1f, 100);

		view.lookAlong(new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));

		projection.mul(view, projectionView);
		updateUniformBlock();
	}

	@Override
	public Matrix4f getView() {
		return view;
	}

	@Override
	public void setView(final Matrix4f view) {
		this.view = view;
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
	public int getUBO() {
		if (UBO == -1) {
			UBO = glGenBuffers();
			glBindBuffer(GL_UNIFORM_BUFFER, UBO);
			GLErrors.checkForError(TAG, "glBindBuffer");
			glBufferData(GL_UNIFORM_BUFFER, 256, GL_DYNAMIC_DRAW);
			GLErrors.checkForError(TAG, "glBufferData");
			glBindBufferBase(GL_UNIFORM_BUFFER, 1, UBO);
			GLErrors.checkForError(TAG, "glBindBufferBase");
			glBindBuffer(GL_UNIFORM_BUFFER, 0);

		}
		return UBO;
	}

	@Override
	public void updateUniformBlock() {
		glBindBuffer(GL_UNIFORM_BUFFER, getUBO());
		GLErrors.checkForError(TAG, "glBindBuffer");
		final FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
		glBufferSubData(GL_UNIFORM_BUFFER, 0, getProjection().get(projectionBuffer));

		final FloatBuffer viewBuffer = BufferUtils.createFloatBuffer(16);
		glBufferSubData(GL_UNIFORM_BUFFER, 64, getView().get(viewBuffer));

		final FloatBuffer projectionViewBuffer = BufferUtils.createFloatBuffer(16);
		glBufferSubData(GL_UNIFORM_BUFFER, 128, getProjectionView().get(projectionViewBuffer));

		final FloatBuffer skyboxViewBuffer = BufferUtils.createFloatBuffer(16);
		glBufferSubData(GL_UNIFORM_BUFFER, 192, getSkyboxView().get(skyboxViewBuffer));
		GLErrors.checkForError(TAG, "glBufferSubData");
		glBindBuffer(GL_UNIFORM_BUFFER, 0);
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
}
