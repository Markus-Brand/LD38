package mbeb.opengldefault.scene;

import java.util.*;

import org.joml.*;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;

/**
 * A "visitor" of the scenegraph to render it
 */
public class SceneGraphRenderer {

	public static final String ViewPosUniformName = "viewPos";
	public static final String ModelMatrixUniformName = "model";

	private static final String TAG = "SceneGraphRenderer";

	protected final SceneObject root;
	protected final ICamera camera;

	/**
	 * create a new renderer for the given SceneGraph and Camera
	 *
	 * @param root
	 * @param camera
	 */
	public SceneGraphRenderer(final SceneObject root, final ICamera camera) {
		this.root = root;
		this.camera = camera;
	}

	/**
	 * render the whole scenegraph
	 */
	public void render() {
		renderObject(root, new Matrix4f());
	}

	/**
	 * renders a given sub-graph with a parent-transformation
	 *
	 * @param object
	 *            the sub-scenegraph to render
	 * @param parentTransform
	 *            the parent transformation for this graph
	 */
	public void renderObject(final SceneObject object, final Matrix4f parentTransform) {
		final Matrix4f transform = parentTransform.mul(object.getTransformation().asMatrix(), new Matrix4f());
		renderSelf(object, transform);
		final Collection<SceneObject> subObjects = object.getSubObjects();
		if (subObjects != null) {
			for (final SceneObject subObject : subObjects) {
				renderObject(subObject, transform);
			}
		}
	}

	/**
	 * render the IRenderable of an object
	 *
	 * @param object
	 *            the object which should be rendered
	 * @param transform
	 *            the model-Transformation for this Renderable
	 */
	public void renderSelf(final SceneObject object, final Matrix4f transform) {
		final Shader shader = object.getShader();
		shader.use();
		if (object.hasOwnShader()) {
			final Vector3f position = camera.getPosition();
			shader.setUniform(ViewPosUniformName, position, true);
		}
		final IRenderable renderable = object.getRenderable();
		if (renderable == null) {
			return;
		}
		final Matrix4f model = transform.mul(renderable.getTransform());
		shader.setUniform(ModelMatrixUniformName, model, true);

		renderable.render(shader);
	}
}
