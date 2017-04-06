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
	/** the followed curve-object */
	private SceneObject curveObj;
	/** the actual curve data */
	private BezierCurve curve;
	/** speed of the object */
	private float speed;
	/** current progress in the curve */
	private float progress;

	public BezierBehaviour(SceneObject curve, float speed) {
		this.curveObj = curve;
		this.speed = speed;
		this.progress = 0;

		this.curve = ((BezierCurveRenderable) curve.getRenderable()).getCurve();
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		progress += deltaTime * speed;
		progress %= curve.getMaxLength();
		Vector3f direction =
				curve.getPosition(progress, curveObj.getGlobalTransformation().asMatrix()).sub(
						entity.getPosition(), new Vector3f());

		entity.setDirection(direction.normalize(new Vector3f()));

		entity.setPosition(entity.getPosition().add(direction, new Vector3f()));
	}

	@Override
	public boolean triggers(IEntity entity) {
		return true;
	}
}
