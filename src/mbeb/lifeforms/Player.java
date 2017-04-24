package mbeb.lifeforms;

import java.lang.Math;

import mbeb.ld38.HealthBarGUI;
import org.joml.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.materials.*;

public class Player extends Lifeform {

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1)
			.rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	private final Material material;
	private final AnimatedMesh mesh;
	private final ShaderProgram animationShader;
	private IHeightSource heightSource;

	public Player(final float healthpoints, final ShaderProgram animationShader, final IHeightSource heightSource) {
		super(0.3f, healthpoints);
		this.animationShader = animationShader;
		material = new Material("material/samurai", 1);
		mesh = new ObjectLoader().loadFromFileAnim("samurai.fbx");
		mesh.setTransform(MeshFlip);
		mesh.getSkeleton().printRecursive("");
		this.heightSource = heightSource;

	}

	public void setHeightSource(final IHeightSource heightSource) {
		this.heightSource = heightSource;
	}

	@Override
	public PlayerEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent, final HealthBarGUI healthGui) {

		final AnimationStateFacade playerAnimatedRenderable = new AnimationStateFacade(mesh, material);

		playerAnimatedRenderable.registerAnimation("Idle", "Idle", 32);
		playerAnimatedRenderable.registerAnimation("Jogging", "Jogging", 32, 0.4f, 0.4f);
		playerAnimatedRenderable.registerAnimation("Pierce", "Pierce", 32, 0.1f, 0.1f, 1.1f);

		final SceneObject playerObject =
				new SceneObject(playerAnimatedRenderable, new BoneTransformation(new Vector3f(1, 10, 0)));

		playerObject.setShader(animationShader);

		parent.addSubObject(playerObject);

		final PlayerEntity playerEntity =
				new PlayerEntity(1f, playerObject, playerAnimatedRenderable, healthpoints, heightSource, healthGui);

		return playerEntity;
	}
}
