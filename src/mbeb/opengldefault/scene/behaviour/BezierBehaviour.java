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
	private BezierCurve curveData;
	/** speed of the object */
	private float speed;
	/** current progress in the curve */
	private float progress;

	public BezierBehaviour(SceneObject curveObj, float speed) {
		this.curveObj = curveObj;
		this.speed = speed;
		this.progress = 0;

		this.curveData = ((BezierCurveRenderable) curveObj.getRenderable()).getCurve();
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		progress += deltaTime * speed;
		progress %= curveData.getTotalLength();
		Vector3f curvePosition = curveData.getPosition(progress, curveObj.getGlobalTransformation().asMatrix());
		Vector3f direction = curvePosition.sub(entity.getPosition(), new Vector3f());

		entity.setDirection(direction.normalize(new Vector3f()));

		entity.setPosition(entity.getPosition().add(direction, new Vector3f()));
	}

	@Override
	public boolean triggers(IEntity entity) {
		return true;
	}
}
