package mbeb.opengldefault.scene;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import mbeb.opengldefault.animation.AnimatedRenderable;
import mbeb.opengldefault.animation.Bone;
import mbeb.opengldefault.animation.Pose;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.io.DataFragment;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.StaticMeshes;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import mbeb.opengldefault.rendering.shader.Shader;

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
	
	@Override
	public void renderSelf(SceneObject object, Matrix4f transform) {
		shader.use();
		
		renderBox(object, transform);
		if (RENDER_BONE_BOXES && object.getRenderable() != null && object.getRenderable().hasAnimations()) {
			Pose pose = object.getRenderable().getCurrentPose();
			renderBoneBoxes(pose.getSkeleton(), pose, transform.mul(pose.getTransform()));
		}
	}
	
	/**
	 * render a single objects bounding box
	 * @param obj
	 * @param boundingBoxTransform 
	 */
	private void renderBox(BoundingBox.Owner obj, Matrix4f boundingBoxTransform){
		if (obj.getBoundingBox().isEmpty()) {
			return;
		}
		Matrix4f localTrans = getBoxTransformFor(obj);
		
		trySettingModelUniform(boundingBoxTransform.mul(localTrans, new Matrix4f()));
		StaticMeshes.getLineCube().render(shader);
		
	}
	
	/**
	 * render the local boundingBoxes for a skeleton
	 */
	private void renderBoneBoxes(Bone skeleton, Pose pose, Matrix4f parentTransform) {
		Matrix4f boneTransform = pose.getRaw(skeleton.getName()).asMatrix();
		Matrix4f transform = parentTransform.mul(boneTransform, new Matrix4f());
		renderBox(skeleton, transform);
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
}
