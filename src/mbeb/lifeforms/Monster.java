package mbeb.lifeforms;

public abstract class Monster extends Lifeform {

	float visionRange;
	float attackRange;
	float attackDamage;
	float attackDuration;
	float movingSpeed;
	PlayerEntity playerEntity;

	public Monster(final float radius, final float healthpoints, final float visionRange, final float attackRange,
			final float attackDamage, final float attackDuration, final float movingSpeed,
			final PlayerEntity playerEntity) {
		super(radius, healthpoints);
		this.visionRange = visionRange;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.attackDuration = attackDuration;
		this.movingSpeed = movingSpeed;
		this.playerEntity = playerEntity;

		//TODO cahnge this
	}

	private float getMovingSpeed() {
		return this.movingSpeed;
	}

	private float getVisionRange() {
		return this.visionRange;
	}

	private Object getAttackRange() {
		return this.attackRange;
	}

}
