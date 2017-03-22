package mbeb.opengldefault.animation;

import java.util.*;

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

		//delete all animators from the list that have finished their animation
		getCurrentAnimations().removeIf(Animator::hasEnded);

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
		synchronized(animatorLock) {
			getCurrentAnimations().add(animator);
		}
	}

	@Override
	public BoundingBox getBoundingBox() {
		return mesh.getBoundingBox();
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

				//mix these animations from stronger strength to lower to minimize artifacts
				List<Animator> currentAnimations = getCurrentAnimations();
				currentAnimations.sort((Animator a1, Animator a2) -> -Double.compare(a1.getCurrentStrength(), a2.getCurrentStrength()));

				for (Animator anim : currentAnimations) {
					Pose p = anim.getCurrentPose();
					p.mixInto(anim.getCurrentStrength(), currentPose);
				}
			}
		}
		return currentPose;
	}

	/**
	 * stop all running animators of a given animation
	 * @param animation
	 */
	public void stopAnimationsOf(Animation animation) {
		synchronized (animatorLock) {
			for (Animator anim: getCurrentAnimations()) {
				if (anim.getAnimation().equals(animation)) {
					anim.stop();
				}
			}
		}
	}

	/**
	 * @param animation
	 * @return true, if an Animator of the given Animation is currently running on this Renderable
	 */
	public boolean hasAnimationsOf(Animation animation) {
		synchronized (animatorLock) {
			for (Animator anim : getCurrentAnimations()) {
				if (anim.getAnimation().equals(animation)) {
					return true;
				}
			}
		}
		return false;
	}
}
