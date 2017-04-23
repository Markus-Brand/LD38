package mbeb.lifeforms;

import org.joml.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;

public abstract class Monster extends Lifeform {

	float visionRange;
	float attackRange;
	float attackDamage;
	float movingSpeed;
	IRenderable body;
	PlayerEntity playerEntity;

	public Monster(final float healthpoints, final float visionRange, final float attackRange, final float attackDamage, final float movingSpeed, final IRenderable body,
			final PlayerEntity playerEntity) {
		super(healthpoints);
		this.visionRange = visionRange;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.movingSpeed = movingSpeed;
		this.body = body;
		this.playerEntity = playerEntity;
	}

	@Override
	public MonsterEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent) {
		final SceneObject monsterObject = new SceneObject(body, new BoneTransformation(position, new Quaternionf(new AxisAngle4f(angle, new Vector3f(0, 1, 0)))));
		parent.addSubObject(monsterObject);
		return new MonsterEntity(monsterObject, healthpoints, visionRange, attackRange, attackDamage, movingSpeed);
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
