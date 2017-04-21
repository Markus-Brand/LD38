package mbeb.opengldefault.scene;

import org.joml.*;
import org.lwjgl.opengl.*;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.gl.shader.*;

public class BoundingBoxRenderer extends VisibleSceneGraphRenderer {

	public static final boolean RENDER_BONE_BOXES = true;

	private static ShaderProgram shader;

	static {
		shader = new ShaderProgram("boundingbox.vert", "boundingbox.frag");
		shader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		shader.setDrawMode(ShaderProgram.DrawMode.LINES);
	}

	public BoundingBoxRenderer(final SceneObject root, final Camera cam) {
		super(root, cam);
	}

	private Matrix4f getBoxTransformFor(final BoundingBox.Owner obj) {
		if (obj.getBoundingBox().isEmpty()) {
			return new Matrix4f();
		}
		return new Matrix4f().translate(obj.getBoundingBox().getLocalStart())
				.scale(obj.getBoundingBox().getLocalSize());
	}

	/** 
	 * renders a single objects bounding box
	 *
	 * @param owner
	 * @param boxColor
	 * @param boundingBoxTransform
	 */
	private void renderBox(final BoundingBox.Owner owner, final Vector3f boxColor, final Matrix4f boundingBoxTransform) {
		if (owner.getBoundingBox().isEmpty()) {
			return;
		}
		final Matrix4f localTrans = getBoxTransformFor(owner);

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
		shader.setUniform(ModelMatrixUniformName, transform, false);
	}

	private Vector3f colorFor(final boolean selected) {
		return selected ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
	}
}
