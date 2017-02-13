package mbeb.opengldefault.animation;

import java.util.ArrayList;
import java.util.List;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * a mesh that has a skeleton
 */
public class AnimatedMesh implements IRenderable {

	private final VAORenderable mesh;
	private final Bone skeleton;
	private List<Animation> animations;

	public AnimatedMesh(VAORenderable mesh, Bone skeleton) {
		this.mesh = mesh;
		this.skeleton = skeleton;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return mesh.getBoundingBox();
	}

	@Override
	public void render(Shader shader) {
		mesh.render(shader);
	}

	private List<Animation> getAnimations() {
		if (animations == null) {
			animations = new ArrayList<>();
		}
		return animations;
	}
	
	public void addAnimation(Animation anim) {
		anim.setSkeleton(skeleton);
		getAnimations().add(anim);
	}

	public Bone getSkeleton() {
		return skeleton;
	}

	/**
	 * return the Animation-object from this mesh with the given name
	 * @param name the name to search for
	 * @return null if no such animation was found
	 */
	public Animation getAnimationByName(String name) {
		for (Animation animation : getAnimations()) {
			if (animation.getName().equals(name)) {
				return animation;
			}
		}
		return null;
	}

}
