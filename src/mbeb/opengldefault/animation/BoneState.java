package mbeb.opengldefault.animation;

import org.joml.Matrix4f;

/**
 * A Bone with some matrices
 */
public class BoneState {

	/** the bone to wrap */
	private Bone bone;

	/** transformation from parent bone to this one */
	private Matrix4f localBoneTransform;
	/** transformation from object origin to the bone */
	private Matrix4f poseBoneTransform;
	/** transformation from bind pose to the bone */
	private Matrix4f combinedBoneTransform;

	public BoneState(Bone bone, Matrix4f localBoneTransform, Matrix4f poseBoneTransform, Matrix4f combinedBoneTransform) {
		this.bone = bone;
		this.localBoneTransform = localBoneTransform;
		this.poseBoneTransform = poseBoneTransform;
		this.combinedBoneTransform = combinedBoneTransform;
	}

	public Bone getBone() {
		return bone;
	}

	public Matrix4f getLocalBoneTransform() {
		return localBoneTransform;
	}

	public Matrix4f getPoseBoneTransform() {
		return poseBoneTransform;
	}

	public Matrix4f getCombinedBoneTransform() {
		return combinedBoneTransform;
	}
}
