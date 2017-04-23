package mbeb.lifeforms;

import java.lang.Math;

import org.joml.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.materials.*;

public abstract class Monster extends Lifeform {

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1).rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	float visionRange;
	float attackRange;
	float attackDamage;
	float attackDuration;
	float movingSpeed;
	PlayerEntity playerEntity;

	Material material;
	AnimatedMesh mesh;
	ShaderProgram animationShader;

	public Monster(final float healthpoints, final float visionRange, final float attackRange, final float attackDamage, final float attackDuration, final float movingSpeed, final IRenderable body,
			final PlayerEntity playerEntity, final ShaderProgram animationShader) {
		super(healthpoints);
		this.visionRange = visionRange;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.attackDuration = attackDuration;
		this.movingSpeed = movingSpeed;
		this.playerEntity = playerEntity;

		//TODO cahnge this
		this.animationShader = animationShader;
		material = new Material("material/samurai", 1);
		mesh = new ObjectLoader().loadFromFileAnim("samurai.fbx");
		mesh.setTransform(MeshFlip);
		mesh.getSkeleton().printRecursive("");
	}

	@Override
	public MonsterEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent) {
		final AnimationStateFacade goblinAnimatedRenderable = new AnimationStateFacade(mesh, material);

		//TODO replace
		goblinAnimatedRenderable.registerAnimation("Idle", "Idle", 32);
		goblinAnimatedRenderable.registerAnimation("Follow", "Follow", 32, 0.4f, 0.4f);
		goblinAnimatedRenderable.registerAnimation("Pierce", "Pierce", 32, 0.1f, 0.1f, 1.1f);

		final SceneObject monsterObject = new SceneObject(goblinAnimatedRenderable, new BoneTransformation(position, new Quaternionf(new AxisAngle4f(angle, new Vector3f(0, 1, 0)))));

		parent.addSubObject(monsterObject);
		return new MonsterEntity(monsterObject, goblinAnimatedRenderable, healthpoints, visionRange, attackRange, attackDamage, attackDuration, movingSpeed);
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
