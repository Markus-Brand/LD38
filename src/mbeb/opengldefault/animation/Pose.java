package mbeb.opengldefault.animation;

import java.util.HashMap;
import java.util.Map;

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
		boneTransforms.putAll(other.boneTransforms);
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
		
		return result;//todo lerp
	}
	
}
