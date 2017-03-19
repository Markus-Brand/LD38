package mbeb.opengldefault.scene;

import java.nio.*;

import com.sun.org.apache.xpath.internal.operations.Mod;
import mbeb.opengldefault.constants.Constants;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.logging.*;
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
		if (RENDER_BONE_BOXES && object.getRenderable() != null && object.getRenderable().hasAnimations()) {
			final Pose pose = object.getRenderable().getCurrentPose();
			renderBoneBoxes(pose.getSkeleton(), pose, transform.mul(pose.getTransform()));
		}
	}

	/**
	 * render the local boundingBoxes for a skeleton
	 */
	private void renderBoneBoxes(final Bone skeleton, final Pose pose, final Matrix4f parentTransform) {
		final Matrix4f boneTransform = pose.getRaw(skeleton.getName()).asMatrix();
		final Matrix4f transform = parentTransform.mul(boneTransform, new Matrix4f());
		renderBox(skeleton, new Vector3f(1, 1, 0), transform);
		for (final Bone childBone : skeleton.getChildren()) {
			renderBoneBoxes(childBone, pose, transform);
		}
	}

	private void trySettingModelUniform(final Matrix4f transform) {
		shader.setUniform(ModelMatrixUniformName, transform);
	}

	private Vector3f colorFor(final boolean selected) {
		return selected == true ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
	}
}
