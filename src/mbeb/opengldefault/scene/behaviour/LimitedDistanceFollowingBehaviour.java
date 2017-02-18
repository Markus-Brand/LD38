package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.Entity;

public class LimitedDistanceFollowingBehaviour extends FollowingBehaviour {

	private float distance;

	public LimitedDistanceFollowingBehaviour(Entity followed, float speed, float distance) {
		super(followed, speed);
		this.distance = distance;
	}

	@Override
	public boolean triggers(Entity entity) {
		return entity.getPosition().distance(getFollowed().getPosition()) < distance;
	}

}
