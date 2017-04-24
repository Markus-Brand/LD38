package mbeb.lifeforms;

import java.awt.Color;

import mbeb.ld38.HealthBarGUI;
import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.FollowingBehaviour;
import mbeb.opengldefault.scene.behaviour.IBehaviour;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.materials.ColorMaterial;
import mbeb.opengldefault.scene.materials.Material;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Goblin extends Monster {

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1)
			.rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	Material material;
	AnimatedMesh mesh;
	ShaderProgram animationShader;

	public Goblin(final PlayerEntity playerEntity, final ShaderProgram animationShader) {
		super(2, 100, 10, 2f, 5, 0.8f, 1f, playerEntity);

		mesh = new ObjectLoader().loadFromFileAnim("goblin.fbx");
		mesh.setTransform(new Matrix4f().rotate(new AxisAngle4f((float)(Math.PI) / -2, 1, 0, 0)));
		mesh.getSkeleton().printRecursive("");
		material = new ColorMaterial(Color.ORANGE);

		this.animationShader = animationShader;

	}

	@Override
	public MonsterEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent, final HealthBarGUI healthBarGui) {
		final AnimationStateFacade goblinAnimatedRenderable = new AnimationStateFacade(mesh, material);

		goblinAnimatedRenderable.registerAnimation("Idle", "Idle", 32);
		goblinAnimatedRenderable.registerAnimation("Run", "Run", 32, 0.4f, 0.4f);
		goblinAnimatedRenderable.registerAnimation("Jump", "Jump", 32, 0.1f, 0.1f, 1.1f);

		final SceneObject monsterObject =
				new SceneObject(goblinAnimatedRenderable, new BoneTransformation(position, new Quaternionf(
						new AxisAngle4f(angle, new Vector3f(0, 1, 0)))));

		monsterObject.setShader(animationShader);

		parent.addSubObject(monsterObject);
		MonsterEntity monster =
				new MonsterEntity(radius, monsterObject, goblinAnimatedRenderable, healthpoints, visionRange,
						attackRange, attackDamage, attackDuration, movingSpeed, healthBarGui);

		System.out.println("DUR" + monster.attackDuration);

		monster.addBehaviour(2, new IBehaviour() {
			@Override
			public void update(final double deltaTime, final IEntity entity) {
				final MonsterEntity goblin = (MonsterEntity) entity;
				goblin.getAnimator().ensureRunning("Idle");
			}
		});

		monster.addBehaviour(1, new FollowingBehaviour(playerEntity, monster.getMovingSpeed()) {
			@Override
			public void update(final double deltaTime, final IEntity entity) {
				super.update(deltaTime, entity);
				final MonsterEntity goblin = (MonsterEntity) entity;

				goblin.getAnimator().ensureRunning("Run");
			}
		}.limited(monster.getVisionRange()));

		monster.addBehaviour(0, new JumpingBehaviour(playerEntity));

		monster.setHealthBarOffset(new Vector3f(0, 2.5f, 0));
		return monster;
	}

}
