package mbeb.opengldefault.animation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.joml.Matrix4f;

/**
 * a bone inside a mesh
 */
public class Bone {
	
	private String name;
	private int index;
	
	private Matrix4f localBindTransform;
	
	private Matrix4f inverseBindTransform = new Matrix4f();
	
	private List<Bone> children;

	public Bone(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public List<Bone> getChildren() {
		if (children == null) {
			children = new ArrayList<>();
		}
		return children;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public Matrix4f getLocalBindTransform() {
		return localBindTransform;
	}

	public void setLocalBindTransform(Matrix4f originalTransform) {
		//System.err.println(getName() + " = " + originalTransform);
		this.localBindTransform = originalTransform;
	}

	public int getIndex() {
		return index;
	}
	
	/**
	 * breadth-first search for a bone with this name
	 * @param name
	 * @return 
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
		
		return null;
	}

	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}
	
	public void updateInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = parentBindTransform.mul(getLocalBindTransform(), new Matrix4f());
		for (Bone child : getChildren()) {
			child.updateInverseBindTransform(bindTransform);
		}
		bindTransform.invert(inverseBindTransform);
	}

	public int boneCount() {
		return 1 + getChildren().stream().map((Bone b) -> b.boneCount()).reduce(0, Integer::sum);
	}
}
