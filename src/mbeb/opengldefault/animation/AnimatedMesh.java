package mbeb.opengldefault.animation;

import java.util.*;

import org.joml.*;

import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

/**
 * a mesh that has a skeleton
 */
public class AnimatedMesh implements IRenderable {

	/** the actual vertex data */
	private final VAORenderable mesh;
	/** the Bone-tree animations rely on */
	private final Bone skeleton;
	/** all animations associated with this mesh */
	private Map<String, Animation> animations;

	/** how much my boundingBox could be bigger than the one of the static mesh */
	private float boundingBoxSizeFactor;
	/** my actual boundingBox */
	private BoundingBox scaledBox;

	public AnimatedMesh(VAORenderable mesh, Bone skeleton) {
		this.mesh = mesh;
		this.skeleton = skeleton;
		this.boundingBoxSizeFactor = 1f;
		scaledBox = null;
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (scaledBox == null) {
			scaledBox = mesh.getBoundingBox().duplicate();
			scaledBox.scale(boundingBoxSizeFactor);
		}
		return scaledBox;
	}

	@Override
	public void render(ShaderProgram shader) {
		mesh.render(shader);
	}

	public VAORenderable getMesh() {
		return mesh;
	}

	public Map<String, Animation> getAnimations() {
		if (animations == null) {
			animations = new HashMap<>();
		}
		return animations;
	}

	public void addAnimation(Animation anim) {
		anim.setSkeleton(skeleton);
		getAnimations().put(anim.getName(), anim);
	}

	public Bone getSkeleton() {
		return skeleton;
	}

	/**
	 * return the Animation-object from this mesh with the given name
	 *
	 * @param name
	 *            the name to search for
	 * @return null if no such animation was found
	 */
	public Animation getAnimationByName(String name) {
		return getAnimations().get(name);
	}

	/**
	 * @return the default bind pose of this mesh
	 */
	public Pose defaultPose() {
		Pose defaultPose = new Pose(skeleton, getTransform());

		skeleton.foreach((Bone bone) -> defaultPose.put(bone.getName(),
				new BoneTransformation(bone.getDefaultBoneTransform())));

		return defaultPose;
	}

	@Override
	public Matrix4f getTransform() {
		return mesh.getTransform();
	}

	/**
	 * set a static transformation for this mesh
	 *
	 * @param transform
	 */
	public void setTransform(Matrix4f transform) {
		mesh.setTransform(transform);
	}

	public void setBoundingBoxSizeFactor(float boundingBoxSizeFactor) {
		this.boundingBoxSizeFactor = boundingBoxSizeFactor;
		scaledBox = null;
	}
}
