package mbeb.opengldefault.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.shader.Shader;

/**
 * A (potentially) complex object inside a scene, with transformations
 */
public class SceneObject {
	
	/** the renderable for this object, or null */
	private Shader shader;
	
	private IRenderable renderable;
	
	private BoundingBox box;
	
	private Transformation myTransformation;
	
	private List<SceneObject> subObjects;
	
	private SceneObject parent;

	/**
	 * Create a new sceneObject. All parameters are optional
	 * @param renderable none
	 * @param myTransformation identity
	 * @param subObjects empty collection
	 */
	public SceneObject(IRenderable renderable, Transformation myTransformation, List<SceneObject> subObjects) {
		this.myTransformation = myTransformation;
		this.subObjects = subObjects;
		this.renderable = renderable;
		box = null;
	}

	/**
	 * @return this objects current Tranformation
	 */
	public Transformation getTransformation() {
		if (myTransformation == null) {
			myTransformation = Transformation.identity();
		}
		return myTransformation;
	}

	/**
	 * add SubObjects via addSubObject instead/**
	 * @return all the Objects that share this objects transformation
	 */
	public List<SceneObject> getSubObjects() {
		if (subObjects == null) {
			subObjects = new ArrayList<>();
		}
		return subObjects;
	}
	
	public void addSubObject(SceneObject object) {
		getSubObjects().add(object);
		object.setParent(this);
		adjustBoundingBoxFor(object);
	}
	
	public void addSubObject(IRenderable model) {
		addSubObject(new SceneObject(model, null, null));
	}

	/**
	 * @return the Renderable of this object, or null
	 */
	public IRenderable getRenderable() {
		return renderable;
	}

	public void setParent(SceneObject parent) {
		this.parent = parent;
	}

	public Shader getShader() {
		if (!hasOwnShader() && parent != null) {
			return parent.getShader();
		}
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}
	
	
	
	public boolean hasOwnShader() {
		return shader != null;
	}
	
	
	
//<editor-fold defaultstate="collapsed" desc="BoundingBox">
	
	
	/**
	 * @return a boundingBox so that each sub-Object lies within
	 */
	public BoundingBox getBoundingBox() {
		//todo check for needed updates
		if (box == null) {
			reCalculateBoundingBox();
		}
		box.setModelTransform(getTransformation());
		return box;
	}
	
	/**
	 * calculate the boundingBox of just the renderable
	 */
	private BoundingBox getRenderableBoundingBox() {
		if (renderable == null) {
			return new BoundingBox.Empty(getTransformation());
		}
		return renderable.getBoundingBox();
	}
	
	/**
	 * completely recalculate my own boundingBox
	 * @return 
	 */
	public BoundingBox reCalculateBoundingBox() {
		box = getRenderableBoundingBox();
		for (SceneObject o: getSubObjects()) {
			adjustBoundingBoxFor(o);
		}
		return box;
	}
	
	/**
	 * insert a given object ot my own boundingBox
	 * @param object 
	 */
	private void adjustBoundingBoxFor(SceneObject object) {
		if (box == null) {
			box = new BoundingBox.Empty(getTransformation());
		}
		box = box.unionWith(object.getBoundingBox());
	}
//</editor-fold>
}
