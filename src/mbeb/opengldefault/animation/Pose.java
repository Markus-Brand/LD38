package mbeb.opengldefault.animation;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import mbeb.opengldefault.rendering.shader.Shader;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * orientations of a bone-construct
 */
public class Pose {
	
	private Map<String, BoneTransformation> boneTransforms = new HashMap<>();
	private Bone skeleton;

	public Pose(Bone skeleton) {
		this.skeleton = skeleton;
	}
	
	/**
	 * store the transformation for a bone, overriding old transformations
	 * @param bone
	 * @param transform
	 * @return this
	 */
	public Pose put(String bone, BoneTransformation transform) {
		boneTransforms.put(bone, transform);
		return this;
	}

	public void mergeWith(Pose other) {
		for (Map.Entry<String, BoneTransformation> transform : other.boneTransforms.entrySet()) {
			if (this.boneTransforms.containsKey(transform.getKey())) {
				 System.err.println("Double key! " + transform.getKey());
			} else {
				this.boneTransforms.put(transform.getKey(), transform.getValue());
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
	 * @param p1
	 * @param p2
	 * @param factor
	 * @return 
	 */
	public static final Pose lerp(Pose p1, Pose p2, double factor) {
		
		assert p1.skeleton == p2.skeleton;
		
		Pose result = new Pose(p1.skeleton);
		
		for (Map.Entry<String, BoneTransformation> boneTransform : p1.boneTransforms.entrySet()) {
			String name = boneTransform.getKey();
			BoneTransformation t1 = boneTransform.getValue();
			BoneTransformation t2 = p2.boneTransforms.get(name);
			
			BoneTransformation resTrans = BoneTransformation.lerp(t1, t2, factor);
			result.put(name, resTrans);
		}
		
		return result;
	}

	public BoneTransformation get(String boneName) {
		return boneTransforms.get(boneName);
	}

	/**
	 * save this pose's data to the specified uniform
	 * @param shader
	 * @param uniformName 
	 */
	public void setUniformData(Shader shader, String uniformName) {
		float[] data = new float[16 * skeleton.boneCount()];
		setUniformData(shader, uniformName, new Matrix4f(
				1, 0, 0, 0,
				0, 0, -1, 0,
				0, 1, 0, 0,
				0, 0, 0, 1), skeleton, data);
		
		
		FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
		buf.put(data);
		buf.flip();
		
		String thisUniform = uniformName;
		//TODO: Check if marix should be transposed
		glUniformMatrix4fv(shader.getUniform(thisUniform), false,  buf);
		System.err.println();
		
		//System.exit(0);
	}
	
	public void setUniformData(Shader shader, String uniformName, Matrix4f parent, Bone bone, float[] data) {
		Matrix4f currentLocalBoneTransform = boneTransforms.get(bone.getName()).asMatrix();
		Matrix4f currentBoneTransform = currentLocalBoneTransform.mul(parent, new Matrix4f());
		for (Bone child : bone.getChildren()) {
			setUniformData(shader, uniformName, currentBoneTransform, child, data);
		}
		
		Matrix4f combined = currentBoneTransform.mul(bone.getInverseBindTransform(), new Matrix4f());
		
		
		/*System.err.println(bone.getName() + " bind = " + bone.getBindTransform());
		System.err.println(bone.getName() + " currentLocalBoneTransform = " + currentLocalBoneTransform);
		System.err.println(bone.getName() + " currentBoneTransform = " + currentBoneTransform);
		System.err.println(bone.getName() + " combined = " + combined);/**/
		
		
		int offset = 16 * bone.getIndex();
		combined.get(data, offset);
	}
	
	
}
