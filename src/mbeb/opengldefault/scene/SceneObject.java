package mbeb.opengldefault.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.Animation;
import mbeb.opengldefault.animation.Animator;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import org.joml.Matrix4f;

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
	private Matrix4f myTransformation;
	/** all subObjects (which inherit transformations) */
	private List<SceneObject> subObjects;
	/** the parent in the Scene-graph */
	private SceneObject parent;
	
	private List<Animator> currentAnimations;

	/**
	 * Create a new sceneObject. All parameters are optional
	 *
	 * @param renderable
	 *            none
	 * @param myTransformation
	 *            identity
	 * @param subObjects
	 *            empty collection
	 */
	public SceneObject(IRenderable renderable, Matrix4f myTransformation, List<SceneObject> subObjects) {
		this.myTransformation = myTransformation;
		this.subObjects = subObjects;
		this.renderable = renderable;
		box = null;
	}

	/**
	 * @return this objects current Tranformation
	 */
	public Matrix4f getTransformation() {
		if (myTransformation == null) {
			myTransformation = new Matrix4f();
		}
		return myTransformation;
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
		addSubObject(new SceneObject(model, null, null));
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

	public List<Animator> getCurrentAnimations() {
		if (currentAnimations == null) {
			currentAnimations = new ArrayList<>();
		}
		return currentAnimations;
	}
	
	/**
	 * return the Animation-object from this mesh with the given name
	 * @param name the name to search for
	 * @return null if no such animation was found
	 */
	public Animation getAnimationByName(String name) {
		if (getRenderable() instanceof AnimatedMesh) {
			AnimatedMesh animatedMesh = (AnimatedMesh) getRenderable();
			return animatedMesh.getAnimationByName(name);
		} else {
			Log.log(TAG, "cant play animation \"" + name + "\" for non-boned mesh.");
			return null;
		}
	}
	
	public void playAnimation(Animator animator) {
		getCurrentAnimations().add(animator);
		//todo update animations maybe?
	}
	
	public void playAnimation(String name, boolean looping, boolean flipping) {
		Animator anim = new Animator(getAnimationByName(name));
		//todo apply animation-flags
		playAnimation(anim);
	}
	
	public void update(double deltaTime) {
		getCurrentAnimations().forEach((Animator anim) -> anim.update(deltaTime));
		getSubObjects().forEach((SceneObject obj) -> obj.update(deltaTime));
	}

//<editor-fold defaultstate="collapsed" desc="BoundingBox">

	/**
	 * @return a boundingBox so that each sub-Object lies within
	 */
	public BoundingBox getBoundingBox() {
		//TODO: Only recalculate BB if needed
		reCalculateBoundingBox();
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
	 *
	 * @return
	 */
	public BoundingBox reCalculateBoundingBox() {
		box = getRenderableBoundingBox();
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
			box = new BoundingBox.Empty(getTransformation());
		}
		box = box.unionWith(object.getBoundingBox());
	}
//</editor-fold>
}
