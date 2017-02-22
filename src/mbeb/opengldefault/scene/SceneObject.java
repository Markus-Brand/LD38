package mbeb.opengldefault.scene;

import java.util.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;

import org.joml.*;

/**
 * A (potentially) complex object inside a scene, with transformations
 */
public class SceneObject {

	private static final String TAG = "SceneObject";

	/** the renderable for this object, or null */
	private Shader shader;
	/** a renderable for this Object, or null */
	private IRenderable renderable;
	/** the combined boundingBox for this object(renderable+subObjects) */
	private BoundingBox box;
	/** this objects Transformation */
	private BoneTransformation transformation;
	/** all subObjects (which inherit transformations) */
	private List<SceneObject> subObjects;
	/** the parent in the Scene-graph */
	private SceneObject parent;

	/**
	 * Create a new sceneObject. All parameters are optional
	 *
	 * @param renderable
	 *            none
	 * @param transformation
	 *            identity
	 * @param subObjects
	 *            empty collection
	 */
	public SceneObject() {
		this(null, new Matrix4f(), null);
	}

	/**
	 * Create a new sceneObject. All parameters are optional
	 *
	 * @param renderable
	 *            Renderable that is drawn if the object is rendered
	 */
	public SceneObject(IRenderable renderable) {
		this(renderable, new Matrix4f(), null);
	}

	/**
	 * Create a new sceneObject. All parameters are optional
	 *
	 * @param renderable
	 *            Renderable that is drawn if the object is rendered
	 * @param myTransformation
	 *            Local Transformation based on Parent
	 */
	public SceneObject(IRenderable renderable, Matrix4f myTransformation) {
		this(renderable, myTransformation, null);
	}

	/**
	 * Create a new sceneObject. All parameters are optional
	 *
	 * @param renderable
	 *            Renderable that is drawn if the object is rendered
	 * @param myTransformation
	 *            Local Transformation based on Parent
	 */
	public SceneObject(IRenderable renderable, BoneTransformation myTransformation) {
		this(renderable, myTransformation, null);
	}

	/**
	 * Create a new sceneObject. All parameters are optional
	 *
	 * @param renderable
	 *            Renderable that is drawn if the object is rendered
	 * @param myTransformation
	 *            Local Transformation based on Parent
	 * @param subObjects
	 *            Children Objects
	 */
	public SceneObject(IRenderable renderable, Matrix4f myTransformation, List<SceneObject> subObjects) {
		this(renderable, new BoneTransformation(myTransformation), subObjects);
	}

	/**
	 * Create a new sceneObject. All parameters are optional
	 *
	 * @param renderable
	 *            Renderable that is drawn if the object is rendered
	 * @param myTransformation
	 *            Local Transformation based on Parent
	 * @param subObjects
	 *            Children Objects
	 */
	public SceneObject(IRenderable renderable, BoneTransformation myTransformation, List<SceneObject> subObjects) {
		this.transformation = myTransformation;
		this.subObjects = subObjects;
		this.renderable = renderable;
		box = null;
	}

	/**
	 * @return this objects current Tranformation
	 */
	public BoneTransformation getTransformation() {
		if (transformation == null) {
			transformation = new BoneTransformation(new Matrix4f());
		}
		return transformation;
	}

	/**
	 * add SubObjects via addSubObject instead
	 *
	 * @return all the Objects that share this objects transformation
	 */
	public List<SceneObject> getSubObjects() {
		if (subObjects == null) {
			subObjects = new ArrayList<>();
		}
		return subObjects;
	}

	/**
	 * add a new Scene-Object as child of this one to the scene
	 *
	 * @param object
	 *            the new object to add
	 */
	public void addSubObject(SceneObject object) {
		getSubObjects().add(object);
		object.setParent(this);
		adjustBoundingBoxFor(object);
	}

	/**
	 * add a new Scene-Object wrapping the provided model as child of this one to the scene
	 *
	 * @param model
	 *            the new model to add
	 */
	public void addSubObject(IRenderable model) {
		addSubObject(new SceneObject(model));
	}

	/**
	 * @return the Renderable of this object, or null
	 */
	public IRenderable getRenderable() {
		return renderable;
	}

	private void setParent(SceneObject parent) {
		this.parent = parent;
	}

	/**
	 * get the shader this object should use (asking the parent if needed)
	 *
	 * @return
	 */
	public Shader getShader() {
		if (!hasOwnShader() && parent != null) {
			return parent.getShader();
		}
		return shader;
	}

	/**
	 * set an own shader for this object
	 *
	 * @param shader
	 */
	public void setShader(Shader shader) {
		this.shader = shader;
	}

	/**
	 * @return if this object has its own shader
	 */
	public boolean hasOwnShader() {
		return shader != null;
	}

	/**
	 * Updates the object
	 *
	 * @param deltaTime
	 *            time since the last update
	 */
	public void update(double deltaTime) {
		if (getRenderable() != null) {
			getRenderable().update(deltaTime);
		}
		getSubObjects().forEach((SceneObject obj) -> obj.update(deltaTime));

	}

	//<editor-fold defaultstate="collapsed" desc="BoundingBox">

	/**
	 * @return a boundingBox so that each sub-Object lies within
	 */
	public BoundingBox getBoundingBox() {
		//TODO: Only recalculate BB if needed
		reCalculateBoundingBox();
		box.setModelTransform(getTransformation().asMatrix());
		return box;
	}

	/**
	 * calculate the boundingBox of just the renderable
	 */
	private BoundingBox getRenderableBoundingBox() {
		if (renderable == null) {
			return new BoundingBox.Empty(getTransformation().asMatrix());
		}
		return renderable.getBoundingBox();
	}

	/**
	 * completely recalculate my own boundingBox
	 *
	 * @return
	 */
	public BoundingBox reCalculateBoundingBox() {
		if (box == null) {
			box = getRenderableBoundingBox().duplicate();
		}
		for (SceneObject o : getSubObjects()) {
			adjustBoundingBoxFor(o);
		}
		return box;
	}

	/**
	 * insert a given object ot my own boundingBox
	 *
	 * @param object
	 */
	private void adjustBoundingBoxFor(SceneObject object) {
		if (box == null) {
			box = new BoundingBox.Empty(getTransformation().asMatrix());
		}
		box = box.unionWith(object.getBoundingBox());
	}

	//</editor-fold>

	/**
	 * Getter for the global Transformation
	 *
	 * @return global Transformation
	 */
	public BoneTransformation getGLobalTransformation() {
		if (parent == null) {
			return getTransformation();
		} else {
			return parent.getGLobalTransformation().and(getTransformation());
		}

	}

	/**
	 * Getter for the position of the center of the BoundingBox
	 *
	 * @return Objects position
	 */
	public Vector3f getPosition() {
		return getGLobalTransformation().applyTo3(new Vector3f());
	}
}
