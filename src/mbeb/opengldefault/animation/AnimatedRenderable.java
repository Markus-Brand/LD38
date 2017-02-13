package mbeb.opengldefault.animation;

import java.util.ArrayList;
import java.util.List;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * an animatedMesh together with some animation-state
 */
public class AnimatedRenderable implements IRenderable {

	private final AnimatedMesh mesh;
	private List<Animator> currentAnimations = new ArrayList<>();

	public AnimatedRenderable(AnimatedMesh mesh) {
		this.mesh = mesh;
		currentAnimations.add(new Animator(mesh.getAnimations().get(0)));
	}

	@Override
	public void render(Shader shader) {
		//update pose uniforms
		Pose p = currentAnimations.get(0).getCurrentPose();
		
		p.setUniformData(shader, "boneTransforms");
		
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
