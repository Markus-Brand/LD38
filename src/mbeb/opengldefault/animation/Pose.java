package mbeb.opengldefault.animation;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.joml.*;

import mbeb.opengldefault.rendering.shader.*;

/**
 * orientations of a bone-construct
 */
public class Pose {

	/** A BoneTransformation for each bone */
	private Map<String, BoneTransformation> boneTransforms = new HashMap<>();
	private Bone skeleton;
	private Matrix4f transform;

	public Pose(Bone skeleton, Matrix4f transform) {
		this.skeleton = skeleton;
		this.transform = transform;
	}

	public Bone getSkeleton() {
		return skeleton;
	}

	public Matrix4f getTransform() {
		return transform;
	}

	/**
	 * store the transformation for a bone, overriding old transformations
	 *
	 * @param bone
	 * @param transform
	 * @return this
	 */
	public Pose put(String bone, BoneTransformation transform) {
		boneTransforms.put(bone, transform);
		return this;
	}

	/**
	 * merge the Bonetransforms of the other pose into this one
	 *
	 * @param other
	 *            where to read BoneTransformations from
	 */
	public void mergeWith(Pose other) {
		for (Map.Entry<String, BoneTransformation> transform : other.boneTransforms.entrySet()) {
			if (this.boneTransforms.containsKey(transform.getKey())) {
				System.err.println("Double key! " + transform.getKey());
			} else {
				this.boneTransforms.put(transform.getKey(), transform.getValue());
			}
		}
	}

	/**
	 * apply this poses relative transformations to a pose before, but only
	 * to bones which have not been animated yet
	 *
	 * @param before
	 *            the pose to alter
	 */
	public void applyAfter(Pose before) {
		assert before.skeleton == this.skeleton;

		for (Map.Entry<String, BoneTransformation> beforeSet : before.boneTransforms.entrySet()) {
			String key = beforeSet.getKey();
			Bone b = skeleton.firstBoneNamed(key);
			BoneTransformation beforeTransform = beforeSet.getValue();
			if (beforeTransform.isSameAs(b.getDefaultBoneTransform(), 0.001f)) {
				BoneTransformation afterTransform = this.boneTransforms.get(key);
				beforeSet.setValue(afterTransform);

			}
		}
	}

	/**
	 * @return how many bones are actually animated, in percent
	 */
	public float getAnimationAmount() {
		AtomicInteger sum = new AtomicInteger();

		skeleton.foreach((Bone b) -> {
			Matrix4f defTrans = b.getDefaultBoneTransform();
			if (!boneTransforms.get(b.getName()).isSameAs(defTrans, 0.001f)) {
				sum.incrementAndGet();
			}
		});

		return sum.get() / (float) skeleton.boneCount();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("Pose(");
		for (Map.Entry<String, BoneTransformation> transform : boneTransforms.entrySet()) {
			s.append(transform.getKey()).append("->").append(transform.getValue()).append("\n");
		}
		return s.append(")").toString();
	}

	/**
	 * lerp between two poses (eg of two keyFrames)
	 *
	 * @param p1
	 * @param p2
	 * @param factor
	 * @return
	 */
	public static final Pose lerp(Pose p1, Pose p2, double factor) {
		if (factor == 1) {
			return p2;
		}
		if (factor == 0) {
			return p1;
		}

		assert p1.skeleton == p2.skeleton;

		Pose result = new Pose(p1.skeleton, p1.transform);

		for (Map.Entry<String, BoneTransformation> boneTransform : p1.boneTransforms.entrySet()) {
			String name = boneTransform.getKey();
			BoneTransformation t1 = boneTransform.getValue();
			BoneTransformation t2 = p2.boneTransforms.get(name);

			BoneTransformation resTrans = BoneTransformation.lerp(t1, t2, factor);
			result.put(name, resTrans);
		}

		return result;
	}

	public BoneTransformation getRaw(String boneName) {
		return boneTransforms.get(boneName);
	}

	/**
	 * get the Treansformation of a Bone
	 * 
	 * @param name
	 * @return
	 */
	public Matrix4f get(String name) {
		return getRaw(name).asMatrix().mul(transform, new Matrix4f());
	}

	/**
	 * save this pose's data to the specified uniform
	 *
	 * @param shader
	 *            the shader to set the uniform to
	 * @param uniformName
	 *            the uniform to store pose-data
	 */
	public void setUniformData(ShaderProgram shader, String uniformName) {
		Matrix4f[] data = new Matrix4f[skeleton.boneCount()];
		setUniformData(transform, skeleton, data);

		shader.setUniform(uniformName, data);
	}

	/**
	 * save a sub-bone of this pose to the given data-array
	 *
	 * @param parent
	 *            the parent pose transformation
	 * @param bone
	 *            the current bone to recurively add
	 * @param data
	 *            the float array to store matrices into
	 */
	private void setUniformData(Matrix4f parent, Bone bone, Matrix4f[] data) {
		Matrix4f currentLocalBoneTransform = getRaw(bone.getName()).asMatrix();
		Matrix4f currentBoneTransform = parent.mul(currentLocalBoneTransform, new Matrix4f());
		for (Bone child : bone.getChildren()) {
			setUniformData(currentBoneTransform, child, data);
		}

		Matrix4f combined = currentBoneTransform.mul(bone.getInverseBindTransform(), new Matrix4f());
		data[bone.getIndex()] = combined;
	}
}
