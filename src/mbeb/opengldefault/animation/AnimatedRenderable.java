package mbeb.opengldefault.animation;

import java.util.*;
import mbeb.opengldefault.logging.Log;

import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

/**
 * an animatedMesh together with some animation-state
 */
public class AnimatedRenderable implements IRenderable {

	private static final String TAG = "AnimatedRenderable";

	private final AnimatedMesh mesh;
	private List<Animator> currentAnimations = new ArrayList<>();
	private final Object animatorLock = new Object();

	public AnimatedRenderable(AnimatedMesh mesh) {
		this.mesh = mesh;

		for (Animation anim : mesh.getAnimations()) {
			System.out.println("Animation: " + anim.getName());
		}

	}

	@Override
	public void render(Shader shader) {
		//update pose uniforms

		Pose finalPose = mesh.defaultPose();

		synchronized (animatorLock) {
			for (Animator anim : getCurrentAnimations()) {
				Pose p = anim.getCurrentPose();
				p.applyAfter(finalPose);
			}
		}

		finalPose.setUniformData(shader, "boneTransforms");

		mesh.render(shader);
	}

	@Override
	public void update(double deltaTime) {
		getCurrentAnimations().forEach((Animator anim) -> anim.update(deltaTime));
		mesh.update(deltaTime);
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
		//todo update animations maybe?
	}

	public void playAnimation(String name, boolean looping, boolean flipping) {
		Animation anim = mesh.getAnimationByName(name);
		if (anim == null) {
			Log.log(TAG, "No animation named: " + name);
			return;
		}
		Animator animator = new Animator(anim);
		animator.setFadeInTime(1);
		animator.setSpeed(10);
		//todo apply animation-flags
		playAnimation(animator);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return mesh.getBoundingBox();//todo boundingbox changes with pose
	}
}
