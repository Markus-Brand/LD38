package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.scene.entities.Entity;

import org.joml.Vector3f;

public class BezierBehaviour extends Behaviour {
	private BezierCurve curve;

	private float speed;
	private float progress;

	public BezierBehaviour(BezierCurve curve, float speed) {
		this.curve = curve;
		this.speed = speed;
		progress = 0;
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
