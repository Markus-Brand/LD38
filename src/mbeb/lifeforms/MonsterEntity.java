package mbeb.lifeforms;

import mbeb.opengldefault.scene.*;

public class MonsterEntity extends LifeformEntity {

	float visionRange;
	float attackRange;
	float attackDamage;
	float movingSpeed;

	public MonsterEntity(final SceneObject monsterObject, final float healthpoints, final float visionRange, final float attackRange, final float attackDamage, final float movingSpeed) {
		super(monsterObject, healthpoints);
		this.visionRange = visionRange;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.movingSpeed = movingSpeed;
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

}
