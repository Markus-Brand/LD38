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

	public FollowingBehaviour(Entity followed, float speed) {
		super(followed);
		this.speed = speed;
	}

	@Override
	public void update(double deltaTime, Entity entity) {
		Vector3f direction = getReference().getPosition().sub(entity.getPosition(), new Vector3f());

		float distance = (float) (speed * deltaTime);

		if (direction.length() == 0) {
			return;
		}

		if (direction.length() > distance) {
			direction.normalize().mul(distance);
		}

		entity.setDirection(direction.normalize(new Vector3f()));

		entity.setPosition(entity.getPosition().add(direction, new Vector3f()));
	}

	@Override
	public boolean triggers(Entity entity) {
		return true;
	}

}
