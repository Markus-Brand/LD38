package mbeb.lifeforms;

import org.joml.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class JumpingBehaviour extends ReferenceEntityBehaviour {
	private static final String TAG = "JumpingBehaviour";

	float attackDuration = -1;
	double stoppingTime;
	boolean isJumping;
	Vector3f startingPosition;
	Vector3f playerPosition;

	public JumpingBehaviour(final PlayerEntity playerEntity) {
		super(playerEntity);
		this.attackDuration = attackDuration;
		this.stoppingTime = -1;
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
		System.out.println("Jump!!!");
		if (!isJumping) {
			stoppingTime = deltaTime + attackDuration;
			startingPosition = goblin.getPosition();
			playerPosition = getReference().getPosition();
			goblin.setDirection(playerPosition.sub(startingPosition, new Vector3f()));
			isJumping = true;
		}
		if (stoppingTime < deltaTime) {
			isJumping = false;
		}
		if (isJumping) {
			goblin.setPosition(startingPosition.lerp(playerPosition, 1.0f - (float) (stoppingTime - deltaTime) / attackDuration, new Vector3f()));
		}

		goblin.getAnimator().ensureRunning("Pierce", isJumping, false);
	}
}
