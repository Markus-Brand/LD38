package mbeb.lifeforms;

import org.joml.*;

import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class Goblin extends Monster {

	public Goblin(final float healthpoints, final float visionRange, final float attackRange, final float attackDamage, final float attackDuration, final float movingSpeed, final IRenderable body,
			final PlayerEntity playerEntity, final ShaderProgram animationShader) {
		super(healthpoints, visionRange, attackRange, attackDamage, attackDuration, movingSpeed, body, playerEntity, animationShader);
	}

	@Override
	public MonsterEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent) {
		final MonsterEntity monster = super.spawnNew(position, angle, parent);
		monster.addBehaviour(2, new IBehaviour() {

			@Override
			public void update(final double deltaTime, final IEntity entity) {
				final MonsterEntity goblin = (MonsterEntity) entity;
				goblin.getAnimator().ensureRunning("Idle");
			}
		}).addBehaviour(1, new FollowingBehaviour(playerEntity, monster.getMovingSpeed()) {

			@Override
			public void update(final double deltaTime, final IEntity entity) {
				super.update(deltaTime, entity);
				final MonsterEntity goblin = (MonsterEntity) entity;

				goblin.getAnimator().ensureRunning("Follow");
			}
		}.limited(monster.getVisionRange())).addBehaviour(0, new JumpingBehaviour(playerEntity).limited(monster.getAttackRange()));
		return monster;
	}

}
