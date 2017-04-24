package mbeb.lifeforms;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.scene.*;
import org.joml.Vector3f;

public class MonsterEntity extends LifeformEntity {

	float radius;
	float visionRange;
	float attackRange;
	float attackDamage;
	float attackDuration;
	float movingSpeed;

	private final AnimationStateFacade animator;

	public MonsterEntity(final float radius, final SceneObject monsterObject, final AnimationStateFacade animator,
			final float healthpoints, final float visionRange, final float attackRange,
			final float attackDamage, final float attackDuration, final float movingSpeed) {
		super(monsterObject, healthpoints, radius);
		this.visionRange = visionRange;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.attackDuration = attackDuration;
		this.movingSpeed = movingSpeed;

		this.animator = animator;
	}

	public float getVisionRange() {
		return visionRange;
	}

	public void setVisionRange(final float visionRange) {
		this.visionRange = visionRange;
	}

	public float getAttackRange() {
		return attackRange;
	}

	public void setAttackRange(final float attackRange) {
		this.attackRange = attackRange;
	}

	public float getAttackDamage() {
		return attackDamage;
	}

	public void setAttackDamage(final float attackDamage) {
		this.attackDamage = attackDamage;
	}

	public float getMovingSpeed() {
		return movingSpeed;
	}

	public void setMovingSpeed(final float movingSpeed) {
		this.movingSpeed = movingSpeed;
	}

	public AnimationStateFacade getAnimator() {
		return animator;
	}
}
