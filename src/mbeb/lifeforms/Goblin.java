package mbeb.lifeforms;

import org.joml.*;

import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.scene.materials.*;

public class Goblin extends Monster {

	public Goblin(final PlayerEntity playerEntity, final ShaderProgram animationShader) {
		super(123, 200, 123456, 0.5f, 1234, 2, 0.5f, new ObjectLoader().loadFromFile("bunny.obj").withMaterial(new Material("material/beach", 1)), playerEntity, animationShader);
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

				goblin.getAnimator().ensureRunning("Run");
			}
		}.limited(monster.getVisionRange())).addBehaviour(0, new JumpingBehaviour(playerEntity).limited(monster.getAttackRange()));
		monster.setHealthBarOffset(new Vector3f(0, 2.5f, 0));
		return monster;
	}

}
