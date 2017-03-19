package mbeb.opengldefault.scene;

import java.nio.*;
import java.util.*;

import mbeb.opengldefault.constants.Constants;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.logging.*;
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
	protected final ICamera cam;

	/**
	 * create a new renderer for the given SceneGraph and Camera
	 *
	 * @param root
	 * @param cam
	 */
	public SceneGraphRenderer(final SceneObject root, final ICamera cam) {
		this.root = root;
		this.cam = cam;
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
			//update camera on first object with this shader only
			final int viewPosUniform = shader.getUniform(ViewPosUniformName, false);
			if (viewPosUniform >= 0) {
				final Vector3f pos = cam.getPosition();
				GL20.glUniform3f(viewPosUniform, pos.x, pos.y, pos.z);
				GLErrors.checkForError(TAG, "glUniform3f");
			}
		}
		final IRenderable renderable = object.getRenderable();
		if (renderable == null) {
			return;
		}
		final int modelUniform = shader.getUniform(ModelMatrixUniformName, false);
		if (modelUniform >= 0) {
			//only if shader wants the model matrix
			final Matrix4f model = transform.mul(renderable.getTransform());
			final FloatBuffer buffer = BufferUtils.createFloatBuffer(Constants.MAT4_COMPONENTS);
			GL20.glUniformMatrix4fv(modelUniform, false, model.get(buffer));
			GLErrors.checkForError(TAG, "glUniformMatrix4fv");
		}

		renderable.render(shader);
	}
}
