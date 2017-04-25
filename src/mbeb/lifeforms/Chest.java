package mbeb.lifeforms;

import java.lang.Math;
import java.util.function.*;

import org.joml.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.materials.*;
import mbeb.opengldefault.sound.*;

public class Chest {
	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1).rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	protected float interactionRadius;

	Material material;
	AnimatedMesh mesh;
	ShaderProgram animationShader;
	PlayerEntity playerEntity;

	public Chest(final ShaderProgram animationShader, final PlayerEntity playerEntity) {
		this.interactionRadius = 3;

		material = new Material("material/chest", 1);
		mesh = new ObjectLoader().loadFromFileAnim("chest.fbx");
		mesh.setTransform(new Matrix4f().rotate(new AxisAngle4f((float) (Math.PI) / 2, 0, 0, 1)));
		//mesh.setTransform(MeshFlip);
		mesh.getSkeleton().printRecursive("");

		this.animationShader = animationShader;
		this.playerEntity = playerEntity;
	}

	public ChestEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent, final Consumer<ChestEntity> consumer, final SoundEnvironment soundEnvironment) {
		final AnimationStateFacade chestAnimatedRenderable = new AnimationStateFacade(mesh, material);

		chestAnimatedRenderable.registerAnimation("Open", "Open", 32, 0, 0);
		chestAnimatedRenderable.registerAnimation("Opened", "Opened", 32, 0, 0);
		//chestAnimatedRenderable.registerAnimation("Close","Close",32,0.1f,0.1f,1.1f);

		final SceneObject chestObject = new SceneObject(chestAnimatedRenderable, new BoneTransformation(position, new Quaternionf(new AxisAngle4f(angle, new Vector3f(0, 1, 0)))));

		chestObject.setShader(animationShader);

		parent.addSubObject(chestObject);
		final ChestEntity chest = new ChestEntity(chestObject, chestAnimatedRenderable, soundEnvironment);
		chest.addBehaviour(1, new ChestBehaviour(playerEntity, consumer).limited(interactionRadius));
		return chest;
	}
}
