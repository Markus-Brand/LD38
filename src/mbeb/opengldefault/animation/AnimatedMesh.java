package mbeb.opengldefault.animation;

import java.util.*;

import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

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

	public List<Animation> getAnimations() {
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
	 *
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

	/**
	 * @return the default bind pose of this mesh
	 */
	public Pose defaultPose() {
		Pose defaultPose = new Pose(skeleton);

		skeleton.foreach((Bone bone) -> {
			defaultPose.put(bone.getName(),
					new BoneTransformation(bone.getDefaultBoneTransform()));
		});

		return defaultPose;
	}

}
