package mbeb.opengldefault.scene.behaviour;

import org.joml.Vector3f;

import mbeb.opengldefault.scene.entities.Entity;

public class FollowingBehaviour extends Behaviour {

	private Entity followed;

	private float speed;

	public FollowingBehaviour(Entity followed, float speed) {
		this.followed = followed;
		this.speed = speed;
	}

	@Override
	public void update(double deltaTime, Entity entity) {
		Vector3f direction = followed.getPosition().sub(entity.getPosition(), new Vector3f());

		entity.setDirection(direction.normalize(new Vector3f()));

		float distance = (float) (speed * deltaTime);

		if (direction.length() > distance) {
			direction.normalize().mul(distance);
		}

		entity.setPosition(entity.getPosition().add(direction, new Vector3f()));
	}

	@Override
	public boolean triggers(Entity entity) {
		return true;
	}

	public Entity getFollowed() {
		return followed;
	}

}
