package mbeb.opengldefault.scene.behaviour;

import org.joml.*;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.rendering.renderable.BezierCurveRenderable;
import mbeb.opengldefault.scene.SceneObject;

import mbeb.opengldefault.scene.entities.*;

/**
 * A Behaviour that makes an Entity follow a {@link BezierCurve} and look in the curves direction
 *
 * @author Markus
 */
public class BezierBehaviour implements IBehaviour {
	/** the followed curve */
	private SceneObject curve;

	/** speed of the object */
	private float speed;
	/** current progress in the curve */
	private float progress;

	public BezierBehaviour(SceneObject curve, float speed) {
		this.curve = curve;
		this.speed = speed;
		this.progress = 0;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		progress += deltaTime * speed;
		progress %= getCurve().getMaxLength();
		Vector3f direction =
				getCurve().getPosition(progress, curve.getGLobalTransformation().asMatrix()).sub(
						entity.getPosition(), new Vector3f());

		entity.setDirection(direction.normalize(new Vector3f()));

		entity.setPosition(entity.getPosition().add(direction, new Vector3f()));
	}

	private BezierCurve getCurve() {
		return ((BezierCurveRenderable) curve.getRenderable()).getCurve();
	}

	@Override
	public boolean triggers(IEntity entity) {
		return true;
	}
}
