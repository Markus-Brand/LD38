package mbeb.opengldefault.scene;

import java.nio.FloatBuffer;
import java.util.Collection;

import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.shader.Shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

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
		renderObject(root, Transformation.identity());
	}

	/**
	 * renders a given sub-graph with a parent-transformation
	 *
	 * @param object
	 *            the sub-scenegraph to render
	 * @param parentTransform
	 *            the parent transformation for this graph
	 */
	public void renderObject(final SceneObject object, final Transformation parentTransform) {
		final Transformation transform = parentTransform.and(object.getTransformation());
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
	public void renderSelf(final SceneObject object, final Transformation transform) {
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
			final Matrix4f model = transform.asMatrix();
			final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
			GL20.glUniformMatrix4fv(modelUniform, false, model.get(buffer));
			GLErrors.checkForError(TAG, "glUniformMatrix4fv");
		}

		renderable.render(shader);
	}
}
