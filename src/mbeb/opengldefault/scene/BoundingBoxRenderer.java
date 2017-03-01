package mbeb.opengldefault.scene;

import java.nio.FloatBuffer;
import mbeb.opengldefault.animation.Bone;
import mbeb.opengldefault.animation.Pose;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.renderable.StaticMeshes;
import mbeb.opengldefault.rendering.shader.Shader;
import org.joml.Vector3f;

public class BoundingBoxRenderer extends VisibleSceneGraphRenderer {

	private static final String TAG = "BoundingBoxRenderer";
	
	public static final boolean RENDER_BONE_BOXES = true;

	private static Shader shader;

	static {
		shader = new Shader("boundingbox.vert", "boundingbox.frag");
		shader.addUniformBlockIndex(1, "Matrices");
		shader.setDrawMode(GL11.GL_LINES);
	}

	public BoundingBoxRenderer(SceneObject root, ICamera cam) {
		super(root, cam);
	}

	private Matrix4f getBoxTransformFor(BoundingBox.Owner obj) {
		if (obj.getBoundingBox().isEmpty()) {
			return new Matrix4f();
		}
		return new Matrix4f()
				.translate(obj.getBoundingBox().getLocalStart())
				.scale(obj.getBoundingBox().getLocalSize());
	}
	
	/**
	 * render a single objects bounding box
	 * @param object
	 * @param boundingBoxTransform 
	 */
	private void renderBox(BoundingBox.Owner object, Vector3f boxColor, Matrix4f boundingBoxTransform){
		if (object.getBoundingBox().isEmpty()) {
			return;
		}
		Matrix4f localTrans = getBoxTransformFor(object);
		
		trySettingModelUniform(boundingBoxTransform.mul(localTrans, new Matrix4f()));
		GL20.glUniform3f(shader.getUniform("boxColor"), boxColor.x, boxColor.y, boxColor.z);
		StaticMeshes.getLineCube().render(shader);
	}
	
	@Override
	public void renderSelf(SceneObject object, Matrix4f transform) {
		shader.use();
		
		renderBox(object, colorFor(object.isSelected()), transform);
		if (RENDER_BONE_BOXES && object.getRenderable() != null && object.getRenderable().hasAnimations()) {
			Pose pose = object.getRenderable().getCurrentPose();
			renderBoneBoxes(pose.getSkeleton(), pose, transform.mul(pose.getTransform()));
		}
	}
	
	/**
	 * render the local boundingBoxes for a skeleton
	 */
	private void renderBoneBoxes(Bone skeleton, Pose pose, Matrix4f parentTransform) {
		Matrix4f boneTransform = pose.getRaw(skeleton.getName()).asMatrix();
		Matrix4f transform = parentTransform.mul(boneTransform, new Matrix4f());
		renderBox(skeleton, new Vector3f(1, 1, 0), transform);
		for (Bone childBone : skeleton.getChildren()) {
			renderBoneBoxes(childBone, pose, transform);
		}
	}
	
	private void trySettingModelUniform(Matrix4f transform) {
		final int modelUniform = shader.getUniform(ModelMatrixUniformName, false);
		if (modelUniform >= 0) {
			//only if shader wants the model matrix
			final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
			GL20.glUniformMatrix4fv(modelUniform, false, transform.get(buffer));
			GLErrors.checkForError(TAG, "glUniformMatrix4fv");
		}
	}
	
	private Vector3f colorFor(boolean selected) {
		return selected == true ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
	}
}
