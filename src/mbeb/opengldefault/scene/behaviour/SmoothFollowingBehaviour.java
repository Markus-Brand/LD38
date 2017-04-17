package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.IEntity;

/**
 * A following-Behaviour with smooth speed increase
 */
public class SmoothFollowingBehaviour extends FollowingBehaviour {

	public SmoothFollowingBehaviour(IEntity followed, float speed) {
		super(followed, speed);
	}

	@Override
	protected float getSpeed(IEntity entity) {
		return super.getSpeed(entity) * getDirectionTo(entity).length();
	}
}
