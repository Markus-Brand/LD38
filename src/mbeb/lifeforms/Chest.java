package mbeb.lifeforms;

import java.lang.Math;

import org.joml.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.materials.*;

public class Chest {
	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1).rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	protected float interactionRadius;

	Material material;
	AnimatedMesh mesh;
	ShaderProgram animationShader;
	PlayerEntity playerEntity;

	public Chest(final float interactionRadius, final ShaderProgram animationShader, final PlayerEntity playerEntity) {
		this.interactionRadius = interactionRadius;

		material = new Material("material/chest", 1);
		mesh = new ObjectLoader().loadFromFileAnim("chest.fbx");
		mesh.setTransform(new Matrix4f().rotate(new AxisAngle4f((float) (Math.PI) / -2, 1, 0, 0)));
		mesh.setTransform(MeshFlip);
		mesh.getSkeleton().printRecursive("");

		this.animationShader = animationShader;
		this.playerEntity = playerEntity;
	}

	public ChestEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent, final SceneObject playerObject, final AnimationStateFacade playerAnimatedRenderable) {
		final AnimationStateFacade chestAnimatedRenderable = new AnimationStateFacade(mesh, material);

		chestAnimatedRenderable.registerAnimation("Idle", "Idle", 32);
		chestAnimatedRenderable.registerAnimation("Open", "Open", 32, 0.4f, 0.4f);
		//chestAnimatedRenderable.registerAnimation("Close","Close",32,0.1f,0.1f,1.1f);

		final SceneObject chestObject = new SceneObject(chestAnimatedRenderable, new BoneTransformation(position, new Quaternionf(new AxisAngle4f(angle, new Vector3f(0, 1, 0)))));

		chestObject.setShader(animationShader);

		parent.addSubObject(chestObject);
		final ChestEntity chest = new ChestEntity(chestObject, chestAnimatedRenderable, 3);
		chest.addBehaviour(1, new ChestBehaviour(playerEntity).limited(interactionRadius));
		return chest;
	}
}
