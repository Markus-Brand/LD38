package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.scene.entities.Entity;

import org.joml.Vector3f;

/**
 * A Behaviour that makes an Entity follow a {@link BezierCurve} and look in the curves direction
 *
 * @author Markus
 */
public class BezierBehaviour implements IBehaviour {
	/** the followed curve */
	private BezierCurve curve;

	/** speed of the object */
	private float speed;
	/** current progress in the curve */
	private float progress;

	public BezierBehaviour(BezierCurve curve, float speed) {
		this.curve = curve;
		this.speed = speed;
		this.progress = 0;
	}

	@Override
	public void update(double deltaTime, Entity entity) {
		progress += deltaTime * speed;
		progress %= curve.getMaxLength();
		Vector3f direction = curve.getPosition(progress).sub(entity.getPosition(), new Vector3f());

		entity.setDirection(direction.normalize(new Vector3f()));

		entity.setPosition(entity.getPosition().add(direction, new Vector3f()));
	}

	@Override
	public boolean triggers(Entity entity) {
		return true;
	}
}
