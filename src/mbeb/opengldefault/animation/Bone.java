package mbeb.opengldefault.animation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import mbeb.opengldefault.logging.Log;
import org.joml.Matrix4f;

/**
 * a bone inside a mesh
 */
public class Bone {
	
	private static final String TAG = "Bone";
	
	private final String name;
	private int index;
	
	/** the inverse bind transform of the bone as given by Ai */
	private Matrix4f inverseBindTransform;
	
	private List<Bone> children;

	/**
	 * create a new bone with given name and index
	 * @param name
	 * @param index 
	 */
	public Bone(String name, int index) {
		this.name = name;
		this.index = index;
	}

	/**
	 * @return all the bones that inherit transformation from this one
	 */
	public List<Bone> getChildren() {
		if (children == null) {
			children = new ArrayList<>();
		}
		return children;
	}

	/**
	 * re-set the index of this bone to another value
	 * @param index the new index of this bone
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return getName() + " - " + getIndex() + "(+" + getChildren().size() + ")";
	}
	
	/**
	 * breadth-first search for a bone with gibven name
	 * @param name the name to search for
	 * @return a Bone-object or null when no bone matched the name
	 */
	public Bone firstBoneNamed(String name) {
		if (getName().equals(name)) {
			return this;
		}
		Queue<Bone> boneQueue = new LinkedList<>();
		boneQueue.add(this);
		
		while (!boneQueue.isEmpty()) {
			Bone bone = boneQueue.remove();
			if (bone.getName().equals(name)) {
				return bone;
			}
			boneQueue.addAll(bone.getChildren());
		}
		Log.log(TAG, "cant find any bone named \"" + name + "\"");
		return null;
	}

	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}

	public void setInverseBindTransform(Matrix4f inverseBindTransform) {
		this.inverseBindTransform = inverseBindTransform;
	}

	/**
	 * @return the recursive number of bones this skeleton has (including this one)
	 */
	public int boneCount() {
		return 1 + getChildren().stream().map(Bone::boneCount).reduce(0, Integer::sum);
	}
}
