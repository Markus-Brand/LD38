package mbeb.opengldefault.animation;

import java.util.*;
import java.util.function.*;

import org.joml.*;

import mbeb.opengldefault.logging.*;

/**
 * a bone inside a mesh
 */
public class Bone {

	private static final String TAG = "Bone";

	private final String name;
	private int index;

	/** the inverse bind transform of the bone as given by Ai */
	private Matrix4f inverseBindTransform;
	/** boneTransformation in bind pose */
	private Matrix4f defaultBoneTransform;

	private List<Bone> children;

	/**
	 * construct a new bone without an index
	 * @param name
	 */
	public Bone(String name) {
		this(name, -1);
	}

	/**
	 * create a new bone with given name and index
	 * 
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
	 * 
	 * @param index
	 *            the new index of this bone
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

	/**
	 * search for a bone
	 * 
	 * @param name
	 *            the name to search for
	 * @return a Bone-object or null when no bone matched the name
	 */
	public Bone firstBoneNamed(final String name) {
		return search(b -> b.getName().equals(name), "cant find any bone named \"" + name + "\"");
	}

	/**
	 * search for a bone
	 * 
	 * @param index
	 *            the id to search for
	 * @return a Bone-object or null when no bone matched the name
	 */
	public Bone firstBoneWithIndex(final int index) {
		return search(b -> b.getIndex() == index, "cant find any bone with index \"" + index + "\"");
	}

	/**
	 * breadth-first search for a Bone with a given condition
	 * @param condition
	 * @return
	 */
	private Bone search(Predicate<? super Bone> condition, String  errorLogMessage) {
		Queue<Bone> boneQueue = new LinkedList<>();
		boneQueue.add(this);
		while(!boneQueue.isEmpty()) {
			Bone bone = boneQueue.remove();
			if (condition.test(bone)) {
				return bone;
			}
			boneQueue.addAll(bone.getChildren());
		}
		if (errorLogMessage != null) {
			Log.log(TAG, errorLogMessage);
		}
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
	 * 
	 * @param action
	 */
	public void foreach(Consumer<Bone> action) {
		action.accept(this);
		for (Bone child : getChildren()) {
			child.foreach(action);
		}
	}

	@Override
	public String toString() {
		return getName() + " - " + getIndex() + " (total " + boneCount() + ")";
	}

	public void printRecursive(String prefix) {
		System.out.println(prefix + this);

		for (Bone child: getChildren()) {
			child.printRecursive(prefix + "  |");
		}
	}
}
