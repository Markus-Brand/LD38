package mbeb.lifeforms;

import org.joml.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class JumpingBehaviour extends ReferenceEntityBehaviour {
	private static final String TAG = "JumpingBehaviour";

	float attackDuration = -1;
	boolean isJumping;
	Vector3f startingPosition;
	Vector3f playerPosition;

	float timePassed;

	public JumpingBehaviour(final PlayerEntity playerEntity) {
		super(playerEntity);
		this.isJumping = false;
	}

	@Override
	public void update(final double deltaTime, final IEntity entity) {
		MonsterEntity goblin = null;
		try {
			goblin = (MonsterEntity) entity;
			attackDuration = goblin.attackDuration;
		} catch(final ClassCastException e) {
			Log.error(TAG, "Entity has to be a MonsterEntity to use " + TAG, e);
		}
		if (!isJumping) {
			startingPosition = new Vector3f(goblin.getPosition());
			playerPosition = new Vector3f(getReference().getPosition());
			System.out.println(startingPosition);
			System.out.println(playerPosition);
			goblin.setDirection(playerPosition.sub(startingPosition, new Vector3f()));
			isJumping = true;
			timePassed = 0;
		} else {
			timePassed += deltaTime;
		}
		if (attackDuration < timePassed) {
			isJumping = false;
		}
		if (isJumping) {
			float progress = 1.0f - (attackDuration - timePassed) / attackDuration;
			goblin.setPosition(startingPosition.lerp(playerPosition, progress, new Vector3f()));
			System.out.println(progress + " " + goblin.getPosition());
		}

		goblin.getAnimator().ensureRunning("Jump", isJumping, false);
		goblin.getAnimator().ensureRunning("Jump", false, false);
	}

	@Override
	public boolean triggers(IEntity entity) {
		return isJumping
				|| entity.getPosition().distance(getReference().getPosition()) < ((MonsterEntity) entity).attackRange;
	}
}
