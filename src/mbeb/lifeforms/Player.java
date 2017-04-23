package mbeb.lifeforms;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.HeightSource;
import mbeb.opengldefault.scene.materials.Material;

public class Player extends Lifeform {

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1)
			.rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	private Material material;
	private AnimatedMesh mesh;
	private ShaderProgram animationShader;
	private HeightSource heightSource;
	private Sword sword;

	public Player(final float healthpoints, ShaderProgram animationShader, HeightSource heightSource, Sword sword) {
		super(healthpoints);
		this.animationShader = animationShader;
		material = new Material("material/samurai", 1);
		mesh = new ObjectLoader().loadFromFileAnim("samurai.fbx");
		mesh.setTransform(MeshFlip);
		mesh.getSkeleton().printRecursive("");
		this.heightSource = heightSource;
		this.sword = sword;

	}

	public void setSword(Sword sword) {
		this.sword = sword;
	}

	public void setHeightSource(HeightSource heightSource) {
		this.heightSource = heightSource;
	}

	@Override
	public PlayerEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent) {

		AnimationStateFacade playerAnimatedRenderable = new AnimationStateFacade(mesh, material);

		playerAnimatedRenderable.registerAnimation("Idle", "Idle", 32);
		playerAnimatedRenderable.registerAnimation("Jogging", "Jogging", 32, 0.4f, 0.4f);
		playerAnimatedRenderable.registerAnimation("Pierce", "Pierce", 32, 0.1f, 0.1f, 1.1f);

		SceneObject playerObject =
				new SceneObject(playerAnimatedRenderable, new BoneTransformation(new Vector3f(1, 10, 0)));

		playerObject.setShader(animationShader);

		parent.addSubObject(playerObject);

		SwordEntity swordEntity = sword.spawnNew(parent, playerObject, playerAnimatedRenderable);

		final PlayerEntity playerEntity = new PlayerEntity(1f, playerObject, playerAnimatedRenderable, healthpoints,
				heightSource, swordEntity);

		return playerEntity;
	}
}
