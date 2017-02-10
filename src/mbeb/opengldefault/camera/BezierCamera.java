package mbeb.opengldefault.camera;

import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.openglcontext.OpenGLContext;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BezierCamera extends Camera {
	/** Class Name Tag */
	private static final String TAG = "BezierCamera";

	private Vector3f worldUp;

	private BezierCurve path;

	private float distanceTraveled;

	private float speed;

	public BezierCamera(BezierCurve path) {
		projection = new Matrix4f();
		projection.perspective((float) Math.PI / 2, OpenGLContext.getWidth() / (float) OpenGLContext.getHeight(), 0.1f, 100);

		this.viewDirection = new Vector3f(1, 0, 0);
		this.worldUp = new Vector3f(0, 1, 0);

		this.path = path;

		this.distanceTraveled = 0;
		this.speed = 4.4f;

		updateView(0);
	}

	private void updateView(double deltaTime) {

		distanceTraveled += deltaTime * speed;

		if (distanceTraveled > path.getMaxLength()) {
			distanceTraveled -= path.getMaxLength();
		}

		position = path.getPosition(distanceTraveled);

		Vector3f center = new Vector3f();
		view = new Matrix4f();
		//position.add(viewDirection, center);

		view.lookAt(position, center, worldUp);
		projectionView = null;
		updateUniformBlock();
	}

	@Override
	public void update(double deltaTime) {
		updateView(deltaTime);
	}

}
