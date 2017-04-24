package mbeb.lifeforms;

import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class GoblinFollowingBehaviour extends FollowingBehaviour {

	public GoblinFollowingBehaviour(final IEntity followed, final float speed) {
		super(followed, speed);
	}

	@Override
	public void update(final double deltaTime, final IEntity entity) {
		super.update(deltaTime, entity);
		final MonsterEntity goblin = (MonsterEntity) entity;

		goblin.getAnimator().ensureRunning("Follow");
	}
}
