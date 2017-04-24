package mbeb.lifeforms;

import org.joml.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class JumpingBehaviour extends ReferenceEntityBehaviour {
	private static final String TAG = "JumpingBehaviour";

	float attackPreperationTime = -1;
	float attackDuration = -1;
	float attackCooldown = -1;
	boolean isActive;
	Vector3f startingPosition;
	Vector3f playerPosition;

	float timePassed;

	public JumpingBehaviour(final PlayerEntity playerEntity) {
		super(playerEntity);
		this.isActive = false;
	}

	@Override
	public void update(final double deltaTime, final IEntity entity) {
		MonsterEntity goblin = null;
		try {
			goblin = (MonsterEntity) entity;
			attackPreperationTime = goblin.attackPreperationTime;
			attackDuration = goblin.attackDuration;
			attackCooldown = goblin.attackCooldown;
		} catch(final ClassCastException e) {
			Log.error(TAG, "Entity has to be a MonsterEntity to use " + TAG, e);
		}
		if (!isActive) {
			startingPosition = new Vector3f(goblin.getPosition());
			playerPosition = new Vector3f(getReference().getPosition());
			goblin.setDirection(playerPosition.sub(startingPosition, new Vector3f()));
			isActive = true;
			timePassed = 0;
		} else {
			timePassed += deltaTime;
		}
		if (timePassed > attackPreperationTime + attackDuration + attackCooldown) {
			isActive = false;
		}
		if (isActive) {
			if (timePassed < attackPreperationTime) {
				goblin.getAnimator().ensureRunning("Run", false, true);

			} else if (timePassed < attackPreperationTime + attackDuration) {
				final float progress = 1.0f - (attackPreperationTime + attackDuration - timePassed) / attackDuration;
				goblin.setPosition(startingPosition.lerp(playerPosition, progress, new Vector3f()));
				goblin.getAnimator().ensureRunning("Jump", isActive, false);
				goblin.getAnimator().ensureRunning("Jump", false, false);
				goblin.AttackSource.play();
			} else {
				goblin.getAnimator().ensureRunning("Run", false, true);

			}
			goblin.startStroke();
		}
	}

	@Override
	public boolean triggers(final IEntity entity) {
		return isActive || entity.getPosition().distance(getReference().getPosition()) < ((MonsterEntity) entity).attackRange;
	}
}
