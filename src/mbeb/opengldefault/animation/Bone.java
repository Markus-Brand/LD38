package mbeb.opengldefault.animation;

import java.util.*;
import java.util.function.Consumer;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.scene.BoundingBox;

import org.joml.*;

/**
 * a bone inside a mesh
 */
public class Bone implements BoundingBox.Owner {
	
	private static final String TAG = "Bone";
	
	private final String name;
	private int index;
	
	/** the inverse bind transform of the bone as given by Ai */
	private Matrix4f inverseBindTransform;
	/** boneTransformation in bind pose */
	private Matrix4f defaultBoneTransform;
	
	private BoundingBox boundingBox;
	
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

	@Override
	public BoundingBox getBoundingBox() {
		if (boundingBox == null) {
			setBoundingBox(new BoundingBox.Empty(getDefaultBoneTransform()));
		}
		return boundingBox;
	}

	@Override
	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
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
		return getName() + " - " + getIndex() + "(total " + boneCount() + ")";
	}
	
	/**
	 * breadth-first search for a bone with given name
	 * @param name the name to search for
	 * @return a Bone-object or null when no bone matched the name
	 */
	public Bone firstBoneNamed(String name) {
		Queue<Bone> boneQueue = new LinkedList<>();
		boneQueue.add(this);
		while(!boneQueue.isEmpty()) {
			Bone bone = boneQueue.remove();
			if (bone.getName().equals(name)) {
				return bone;
			}
			boneQueue.addAll(bone.getChildren());
		}
		Log.log(TAG, "cant find any bone named \"" + name + "\"");
		return null;
	}
	
	/**
	 * breadth-first search for a bone with given id
	 * @param index the id to search for
	 * @return a Bone-object or null when no bone matched the name
	 */
	public Bone firstBoneWithIndex(int index) {
		Queue<Bone> boneQueue = new LinkedList<>();
		boneQueue.add(this);
		while(!boneQueue.isEmpty()) {
			Bone bone = boneQueue.remove();
			if (bone.getIndex() == index) {
				return bone;
			}
			boneQueue.addAll(bone.getChildren());
		}
		Log.log(TAG, "cant find any bone with index \"" + index + "\"");
		return null;
	}

	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}

	public void setInverseBindTransform(Matrix4f inverseBindTransform) {
		this.inverseBindTransform = inverseBindTransform;
	}

	public Matrix4f getDefaultBoneTransform() {
		return defaultBoneTransform;
	}

	public void setDefaultBoneTransform(Matrix4f defaultBoneTransform) {
		this.defaultBoneTransform = defaultBoneTransform;
	}

	/**
	 * @return the recursive number of bones this skeleton has (including this one)
	 */
	public int boneCount() {
		return 1 + getChildren().stream().map(Bone::boneCount).reduce(0, Integer::sum);
	}
	
	/**
	 * perform an action for every bone in this skeleton
	 * @param action 
	 */
	public void foreach(Consumer<Bone> action) {
		action.accept(this);
		for (Bone child : getChildren()) {
			child.foreach(action);
		}
	}
}
