package mbeb.lifeforms;

import mbeb.ld38.*;
import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.scene.*;

public class MonsterEntity extends LifeformEntity {

	float radius;
	float visionRange;
	float attackRange;
	float attackDamage;
	float attackPreperationTime;
	float attackDuration;
	float attackCooldown;
	float movingSpeed;

	private SwordEntity swordEntity;

	private final AnimationStateFacade animator;

	public MonsterEntity(final float radius, final SceneObject monsterObject, final AnimationStateFacade animator, final float healthpoints, final float visionRange, final float attackRange,
			final float attackDamage, final float attackPreperationTime, final float attackDuration, final float attackCooldown, final float movingSpeed, final HealthBarGUI healthGui) {
		super(monsterObject, healthpoints, radius, healthGui);
		this.visionRange = visionRange;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.attackPreperationTime = attackPreperationTime;
		this.attackDuration = attackDuration;
		this.attackCooldown = attackCooldown;
		this.movingSpeed = movingSpeed;

		this.animator = animator;

		setSword(new Sword(10, 1, 6, LootType.Steel, SwordType.DAGGER_REVERSE));
	}

	@Override
	public void update(final double deltaTime) {
		super.update(deltaTime);
		swordEntity.update(deltaTime);
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

	public void setSword(final Sword sword) {
		setSwordEntity(sword.spawnNew(getSceneObject().getParent(), getSceneObject(), animator));
	}

	public void setSwordEntity(final SwordEntity swordEntity) {
		if (this.swordEntity != null) {
			for (final LifeformEntity tarsched : this.swordEntity.getTarscheds().keySet()) {
				swordEntity.addTarsched(tarsched);
			}
			this.swordEntity.getSceneObject().removeSelf();
		}
		this.swordEntity = swordEntity;
	}

	public SwordEntity getSword() {
		return swordEntity;
	}

	public void startStroke() {
		if (!swordEntity.isStriking()) {
			swordEntity.startStriking();
			//			getAnimator().setDuration("Pierce", swordEntity.getStrokeTime());
			//			getAnimator().ensureRunning("Pierce", true, false);
			//			getAnimator().ensureRunning("Pierce", false, false);
		}
	}

	public void addTarsched(final LifeformEntity tarsched) {
		swordEntity.addTarsched(tarsched);
	}
}
