package mbeb.opengldefault.scene;

import org.joml.*;
import org.lwjgl.opengl.*;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;

public class BoundingBoxRenderer extends VisibleSceneGraphRenderer {

	private static final String TAG = "BoundingBoxRenderer";

	public static final boolean RENDER_BONE_BOXES = true;

	private static Shader shader;

	static {
		shader = new Shader("boundingbox.vert", "boundingbox.frag");
		shader.addUniformBlockIndex("Matrices");
		shader.setDrawMode(GL11.GL_LINES);
	}

	public BoundingBoxRenderer(final SceneObject root, final ICamera cam) {
		super(root, cam);
	}

	private Matrix4f getBoxTransformFor(final BoundingBox.Owner obj) {
		if (obj.getBoundingBox().isEmpty()) {
			return new Matrix4f();
		}
		return new Matrix4f().translate(obj.getBoundingBox().getLocalStart()).scale(obj.getBoundingBox().getLocalSize());
	}

	/**
	 * render a single objects bounding box
	 * 
	 * @param object
	 * @param boundingBoxTransform
	 */
	private void renderBox(final BoundingBox.Owner object, final Vector3f boxColor, final Matrix4f boundingBoxTransform) {
		if (object.getBoundingBox().isEmpty()) {
			return;
		}
		final Matrix4f localTrans = getBoxTransformFor(object);

		trySettingModelUniform(boundingBoxTransform.mul(localTrans, new Matrix4f()));
		shader.setUniform("boxColor", boxColor);
		StaticMeshes.getLineCube().render(shader);
	}

	@Override
	public void renderSelf(final SceneObject object, final Matrix4f transform) {
		shader.use();
		renderBox(object, colorFor(object.isSelected()), transform);
	}

	private void trySettingModelUniform(final Matrix4f transform) {
		shader.setUniform(ModelMatrixUniformName, transform, true);
	}

	private Vector3f colorFor(final boolean selected) {
		return selected ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
	}
}
