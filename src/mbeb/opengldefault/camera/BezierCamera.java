package mbeb.opengldefault.camera;

import mbeb.opengldefault.curves.*;
import mbeb.opengldefault.openglcontext.*;

import org.joml.*;

/**
 * A Camera that follows a path defined by a {@link BezierCurve}
 *
 * @author Markus
 */
public class BezierCamera extends Camera {
	/** Class Name Tag */
	private static final String TAG = "BezierCamera";

	private Vector3f worldUp;

	private BezierCurve path;

	private float distanceTravelled;

	private float speed;

	public BezierCamera(BezierCurve path) {
		projection = new Matrix4f();
		projection.perspective((float) java.lang.Math.PI / 2,
				OpenGLContext.getWidth() / (float) OpenGLContext.getHeight(), 0.1f, 100);

		this.viewDirection = new Vector3f(1, 0, 0);
		this.worldUp = new Vector3f(0, 1, 0);

		this.path = path;

		this.distanceTravelled = 0;
		this.speed = 3.4f;

		updateView(0);
	}

	/**
	 * Updates the view matrix
	 *
	 * @param deltaTime
	 */
	private void updateView(double deltaTime) {

		distanceTravelled += deltaTime * speed;

		if (distanceTravelled > path.getMaxLength()) {
			distanceTravelled -= path.getMaxLength();
		}

		position = path.getPosition(distanceTravelled);

		Vector3f center = new Vector3f();
		view = new Matrix4f();

		view.lookAt(position, center, worldUp);
		projectionView = null;
		updateUniformBlock();
	}

	@Override
	public void update(double deltaTime) {
		updateView(deltaTime);
	}

}
