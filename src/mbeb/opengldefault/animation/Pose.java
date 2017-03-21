package mbeb.opengldefault.animation;

import static org.lwjgl.opengl.GL20.*;

import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.rendering.shader.*;
import java.util.concurrent.atomic.*;

import org.joml.*;
import org.lwjgl.*;

import mbeb.opengldefault.rendering.shader.*;

/**
 * orientations of a bone-construct
 */
public class Pose {

	private static final String TAG = "Pose";
	private static final int FLOATS_PER_MAT4 = 16;

	/** A BoneTransformation for each bone */
	private Map<String, BoneTransformation> boneTransforms = new HashMap<>();
	/** The priority of each bone */
	private Map<String, Integer> bonePriorities = new HashMap<>();
	private Bone skeleton;
	private Matrix4f transform;

	/** the applied bone matrices */
	private BoneState[] convertedData = null;

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

	public int getBonePriority(String boneName) {
		Integer priority = bonePriorities.get(boneName);
		return (priority != null) ? priority : 0;
	}

	public void setBonePriority(String boneName, int value) {
		bonePriorities.put(boneName, value);
	}

	/**
	 * apply this poses relative transformations to a pose before, but only
	 * to bones with lower priority
	 *
	 * @param ownStrength the strength (intensity factor) of <code>this</code> Pose
	 * @param before the pose to alter
	 */
	public void mixInto(final double ownStrength, final Pose before) {
		Log.assertEqual(TAG, this.skeleton, before.skeleton, "Cannot merge poses with different skeletons");

		for (Map.Entry<String, BoneTransformation> beforeSet : before.boneTransforms.entrySet()) {
			String key = beforeSet.getKey();
			BoneTransformation beforeTransform = beforeSet.getValue();

			if (getBonePriority(key) > before.getBonePriority(key)) {
				BoneTransformation afterTransform = this.boneTransforms.get(key);
				if (afterTransform == null) {
					continue;
				}
				BoneTransformation lerped = BoneTransformation.lerp(
						beforeTransform,
						afterTransform, ownStrength);
				beforeSet.setValue(lerped);
				before.bonePriorities.put(key, getBonePriority(key));
			}
		}
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
		if (factor == 0) {
			return p1;
		}
		if (factor == 1) {
			return p2;
		}

		Log.assertEqual(TAG, p1.skeleton, p2.skeleton, "Cannot lerp poses with different skeletons");

		Pose result = new Pose(p1.skeleton, p1.transform);
		result.bonePriorities = p1.bonePriorities;

		for (Map.Entry<String, BoneTransformation> boneTransform : p1.boneTransforms.entrySet()) {
			String name = boneTransform.getKey();
			BoneTransformation t1 = boneTransform.getValue();
			BoneTransformation t2 = p2.boneTransforms.get(name);

			if (t1 != null && t2 != null) {
				BoneTransformation resTrans = BoneTransformation.lerp(t1, t2, factor);
				result.put(name, resTrans);
			}
		}

		return result;
	}

	public BoneTransformation getRaw(String boneName) {
		return boneTransforms.get(boneName);
	}

	/**
	 * get the Transformation of a Bone
	 *
	 * @return
	 */
	public BoneState get(int boneID) {
		return getConvertedData()[boneID];
	}

	public BoneState[] getConvertedData() {
		if (convertedData == null) {
			convertedData = new BoneState[skeleton.boneCount()];
			convertData(transform, skeleton);
		}
		return convertedData;
	}

	private void convertData(Matrix4f parent, Bone bone) {
		if (bone.getIndex() < 0) {
			return;
		}

		Matrix4f currentLocalBoneTransform = getRaw(bone.getName()).asMatrix();
		Matrix4f currentBoneTransform = parent.mul(currentLocalBoneTransform, new Matrix4f());

		for (Bone child : bone.getChildren()) {
			convertData(currentBoneTransform, child);
		}

		Matrix4f combined = currentBoneTransform.mul(bone.getInverseBindTransform(), new Matrix4f());
		convertedData[bone.getIndex()] = new BoneState(bone, currentLocalBoneTransform, currentBoneTransform, combined);
	}

	/**
	 * save this pose's data to the specified uniform
	 *
	 * @param shader
	 *            the shader to set the uniform to
	 * @param uniformName
	 *            the uniform to store pose-data
	 */
	public void setUniformData(Shader shader, String uniformName) {
		float[] data = new float[FLOATS_PER_MAT4 * skeleton.boneCount()];
		BoneState[] transforms = getConvertedData();

		for (int b = 0; b < transforms.length; b++) {
			transforms[b].getCombinedBoneTransform().get(data, b * FLOATS_PER_MAT4);
		}

		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();

		glUniformMatrix4fv(shader.getUniform(uniformName), false, buffer);
		GLErrors.checkForError(TAG, "glUniformMatrix4fv");
	}
}
