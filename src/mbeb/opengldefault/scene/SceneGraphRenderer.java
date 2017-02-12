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
	public SceneGraphRenderer(SceneObject root, ICamera cam) {
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
	public void renderObject(SceneObject object, Transformation parentTransform) {
		Transformation transform = parentTransform.and(object.getTransformation());
		renderSelf(object, transform);
		Collection<SceneObject> subObjects = object.getSubObjects();
		if (subObjects != null) {
			for (SceneObject subObject : subObjects) {
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
	 *            the modle-Transformation for this Renderable
	 */
	public void renderSelf(SceneObject object, Transformation transform) {
		Shader shader = object.getShader();
		shader.use();
		if (object.hasOwnShader()) {
			//update camera on first object with this shader only
			int viewPosUniform = shader.getUniform(ViewPosUniformName);
			if (viewPosUniform >= 0) {
				Vector3f pos = cam.getPosition();
				GL20.glUniform3f(viewPosUniform, pos.x, pos.y, pos.z);
				GLErrors.checkForError(TAG, "glUniform3f");
			}
		}
		IRenderable renderable = object.getRenderable();
		if (renderable == null) {
			return;
		}
		int modelUniform = shader.getUniform(ModelMatrixUniformName);
		if (modelUniform >= 0) {
			//only if shader wants the model matrix
			Matrix4f model = transform.asMatrix();
			FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
			GL20.glUniformMatrix4fv(modelUniform, false, model.get(buffer));
			GLErrors.checkForError(TAG, "glUniformMatrix4fv");
		}

		renderable.render(shader);
	}
}
