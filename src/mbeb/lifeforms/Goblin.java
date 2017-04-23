package mbeb.lifeforms;

import org.joml.*;

import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;

public class Goblin extends Monster {

	public Goblin(final float healthpoints, final float visionRange, final float attackRange, final float attackDamage, final float movingSpeed, final IRenderable body,
			final PlayerEntity playerEntity) {
		super(healthpoints, visionRange, attackRange, attackDamage, movingSpeed, body, playerEntity);
	}

	@Override
	public MonsterEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent) {
		final MonsterEntity monster = super.spawnNew(position, angle, parent);
		monster.addBehaviour(1, new FollowingBehaviour(playerEntity, monster.getMovingSpeed()).limited(monster.getVisionRange())).addBehaviour(0,
				new JumpingBehaviour(playerEntity).limited(monster.getAttackRange()));
		return monster;
	}

}
