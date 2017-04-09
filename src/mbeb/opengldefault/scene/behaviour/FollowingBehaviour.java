package mbeb.opengldefault.scene.behaviour;

import org.joml.*;

import mbeb.opengldefault.scene.entities.*;

/**
 * A Behaviour that makes a Entity follow another Entity
 * 
 * @author Markus
 */
public class FollowingBehaviour extends ReferenceEntityBehaviour {

	/** speed of the entity */
	private float speed;

	public FollowingBehaviour(IEntity followed, float speed) {
		super(followed);
		this.speed = speed;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		Vector3f direction = getDirectionTo(entity);

		float distance = (float) (getSpeed(entity) * deltaTime);

		if (direction.length() == 0) {
			return;
		}

		if (direction.length() > distance) {
			direction.normalize();
			entity.setDirection(direction);
			direction.mul(distance);
		}

		entity.setPosition(entity.getPosition().add(direction, new Vector3f()));
	}

	protected float getSpeed(IEntity entity) {
		return speed;
	}

	protected Vector3f getDirectionTo(IEntity entity) {
		return getReference().getPosition().sub(entity.getPosition(), new Vector3f());
	}

}
