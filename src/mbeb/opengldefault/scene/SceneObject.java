package mbeb.opengldefault.scene;

import java.util.*;

import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.IEntityConvertable;
import mbeb.opengldefault.scene.entities.SceneEntity;
import org.joml.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.gl.shader.*;

/**
 * A (potentially) complex object inside a scene, with transformations
 */
public class SceneObject implements BoundingBox.Owner, IEntityConvertable {

	private static final String TAG = "SceneObject";

	/** the renderable for this object, or null */
	private ShaderProgram shader;
	/** a renderable for this Object, or null */
	private IRenderable renderable;
	/** the combined boundingBox for this object(renderable+subObjects) */
	private BoundingBox box;
	/** this objects Transformation */
	private BoneTransformation transformation;
	/** cached version of the global Transformation */
	private BoneTransformation globalTransformation = null;
	/** all subObjects (which inherit transformations) */
	private List<SceneObject> subObjects;
	/** the parent in the Scene-graph */
	private SceneObject parent;
	/** true, if the user hovers over the object currently */
	private boolean selected;

	/**
	 * Create a new sceneObject. All parameters are optional
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
	public SceneObject(IRenderableHolder renderable) {
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
	public SceneObject(IRenderableHolder renderable, Matrix4f myTransformation) {
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
	public SceneObject(IRenderableHolder renderable, BoneTransformation myTransformation) {
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
	public SceneObject(IRenderableHolder renderable, Matrix4f myTransformation, List<SceneObject> subObjects) {
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
	public SceneObject(IRenderableHolder renderable, BoneTransformation myTransformation, List<SceneObject> subObjects) {
		this.transformation = myTransformation;
		this.subObjects = subObjects;
		this.renderable = renderable != null ? renderable.getRenderable() : null;
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

	public void setTransformation(BoneTransformation transformation) {
		this.transformation = transformation;
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
	public ShaderProgram getShader() {
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
	public void setShader(ShaderProgram shader) {
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
		getSubObjects().forEach((obj) -> obj.update(deltaTime));

	}

	//<editor-fold defaultstate="collapsed" desc="BoundingBox">

	/**
	 * @return a boundingBox so that each sub-Object lies within
	 */
	@Override
	public BoundingBox getBoundingBox() {
		if (box == null) {
			reCalculateBoundingBox();
		}
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
		box = getRenderableBoundingBox();

		for (SceneObject subObject : getSubObjects()) {
			adjustBoundingBoxFor(subObject);
		}
		return box;
	}

	/**
	 * insert a given object into my own boundingBox
	 *
	 * @param object
	 */
	private void adjustBoundingBoxFor(SceneObject object) {
		if (box == null) {
			box = getRenderableBoundingBox();
		}
		if (object.getBoundingBox() == null) {
			return;
		}
		box = box.unionWith(object.getBoundingBox());
	}

	//</editor-fold>

	public BoneTransformation getParentGlobalTranform() {
		if (parent == null) {
			return BoneTransformation.identity();
		}
		return parent.getGlobalTransformation();
	}

	/**
	 * Getter for the global Transformation
	 *
	 * @return global Transformation
	 */
	public BoneTransformation getGlobalTransformation() {
		if (globalTransformation == null) {
			if (parent == null) {
				globalTransformation = getTransformation();
			} else {
				globalTransformation = getParentGlobalTranform().and(getTransformation());
			}
		}
		return globalTransformation;
	}

	public void invalidatePosition() {
		globalTransformation = null;
		box = null;
		getSubObjects().forEach(SceneObject::invalidatePosition);
	}

	/**
	 * Getter for the position of this object in global space
	 *
	 * @return Objects position
	 */
	public Vector3f getGlobalPosition() {
		return getGlobalTransformation().getPosition();
	}

	/**
	 * @param newPosition
	 *            a new position for this sceneObject (in global space)
	 *            =======
	 * @param newPosition
	 *            a new position for this sceneObject (in global space)
	 *            >>>>>>> hotfix/object-deletion-fix
	 *            =======
	 * @param newPosition
	 *            a new position for this sceneObject (in global space)
	 *            >>>>>>> master
	 */
	public void setGlobalPosition(Vector3f newPosition) {
		Matrix4f inverseMatrix = getParentGlobalTranform().asMatrix().invert(new Matrix4f());
		getTransformation().setPosition(inverseMatrix.transform(new Vector4f(newPosition, 1)));
		invalidatePosition();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void removeSubObject(SceneObject curveObj) {
		subObjects.remove(curveObj);
	}

	@Override
	public IEntity asNewEntity() {
		return new SceneEntity(this);
	}
}
