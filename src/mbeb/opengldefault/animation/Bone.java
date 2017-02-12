package mbeb.opengldefault.animation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * a bone inside a mesh
 */
public class Bone {
	
	private String name;
	private int index;
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

	public int getIndex() {
		return index;
	}
}
