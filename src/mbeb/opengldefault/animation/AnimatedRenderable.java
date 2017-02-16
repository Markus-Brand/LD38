package mbeb.opengldefault.animation;

import java.util.*;

import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

/**
 * an animatedMesh together with some animation-state
 */
public class AnimatedRenderable implements IRenderable {

	private final AnimatedMesh mesh;
	private List<Animator> currentAnimations = new ArrayList<>();

	public AnimatedRenderable(AnimatedMesh mesh) {
		this.mesh = mesh;
		if (mesh.getAnimations().size() > 0) {
			currentAnimations.add(new Animator(mesh.getAnimations().get(0)));
		}
	}

	@Override
	public void render(Shader shader) {
		//update pose uniforms

		for (Animator anim : getCurrentAnimations()) {
			Pose p = anim.getCurrentPose();
			p.setUniformData(shader, "boneTransforms");
		}

		mesh.render(shader);
	}

	@Override
	public void update(double deltaTime) {
		getCurrentAnimations().forEach((Animator anim) -> anim.update(deltaTime));
	}

	public List<Animator> getCurrentAnimations() {
		if (currentAnimations == null) {
			currentAnimations = new ArrayList<>();
		}
		return currentAnimations;
	}

	public void playAnimation(Animator animator) {
		getCurrentAnimations().add(animator);
		//todo update animations maybe?
	}

	public void playAnimation(String name, boolean looping, boolean flipping) {
		Animator anim = new Animator(mesh.getAnimationByName(name));
		//todo apply animation-flags
		playAnimation(anim);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return mesh.getBoundingBox();//todo boundingbox changes with pose
	}
}
