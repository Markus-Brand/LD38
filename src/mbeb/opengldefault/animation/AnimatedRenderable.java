package mbeb.opengldefault.animation;

import java.util.*;
import mbeb.opengldefault.logging.Log;

import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;
import org.joml.Matrix4f;

/**
 * an animatedMesh together with some animation-state
 */
public class AnimatedRenderable implements IRenderable {

	private static final String TAG = "AnimatedRenderable";

	private final AnimatedMesh mesh;
	private List<Animator> currentAnimations = new ArrayList<>();
	private final Object animatorLock = new Object();

	private BoundingBox animatedBoundingBox = null;
	private Pose currentPose = null;

	public AnimatedRenderable(AnimatedMesh mesh) {
		this.mesh = mesh;
	}

	public AnimatedMesh getAnimatedMesh() {
		return mesh;
	}

	@Override
	public void render(Shader shader) {
		//update pose uniforms
		getCurrentPose().setUniformData(shader, "boneTransforms");
		mesh.render(shader);
	}

	@Override
	public void update(double deltaTime) {
		getCurrentAnimations().forEach((Animator anim) -> anim.update(deltaTime));
		mesh.update(deltaTime);
		animatedBoundingBox = null;
		currentPose = null;
	}

	public List<Animator> getCurrentAnimations() {
		if (currentAnimations == null) {
			currentAnimations = new ArrayList<>();
		}
		return currentAnimations;
	}

	public void playAnimation(Animator animator) {
		synchronized (animatorLock) {
			getCurrentAnimations().add(animator);
		}
	}

	public void playAnimation(String name, boolean looping, boolean flipping) {
		Animation anim = mesh.getAnimationByName(name);
		if (anim == null) {
			Log.log(TAG, "No animation named: " + name);
			return;
		}
		Animator animator = new Animator(anim);
		animator.setFadeInTime(1);
		animator.setSpeed(4);
		//todo apply animation-flags
		playAnimation(animator);
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (animatedBoundingBox == null) {
			animatedBoundingBox = adjustWith(
					new BoundingBox.Empty(),
					mesh.getSkeleton(),
					getCurrentPose().getTransform());
		}
		return animatedBoundingBox;
	}
	
	/**
	 * adjust the boundingBox recursively for a given skeleton
	 * @param box the initial box
	 * @param bone the skeleton to insert into the box
	 * @param parentTransform
	 * @return a larger box
	 */
	private BoundingBox adjustWith(BoundingBox box, Bone bone, Matrix4f parentTransform) {
		Matrix4f boneTransform = getCurrentPose().getRaw(bone.getName()).asMatrix();
		Matrix4f transform = parentTransform.mul(boneTransform, new Matrix4f());
		
		BoundingBox boneBox = bone.getBoundingBox();
		boneBox.setModelTransform(transform);
		box = box.unionWith(boneBox);
		
		for (Bone childBone : bone.getChildren()) {
			box = adjustWith(box, childBone, transform);
		}
		
		return box;
	}

	@Override
	public boolean hasAnimations() {
		return true;
	}

	@Override
	public Pose getCurrentPose() {
		if (currentPose == null) {
			currentPose = mesh.defaultPose();
			synchronized (animatorLock) {
				for (Animator anim : getCurrentAnimations()) {
					Pose p = anim.getCurrentPose();
					p.applyAfter(currentPose);
				}
			}
		}
		return currentPose;
	}

}
