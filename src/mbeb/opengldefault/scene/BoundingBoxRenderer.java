package mbeb.opengldefault.scene;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.io.DataFragment;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import mbeb.opengldefault.rendering.shader.Shader;

public class BoundingBoxRenderer extends VisibleSceneGraphRenderer {

	private static final String TAG = "BoundingBoxRenderer";

	private static Shader shader;

	static {
		shader = new Shader("boundingbox.vert", "boundingbox.frag");
		shader.addUniformBlockIndex(1, "Matrices");
		shader.setDrawMode(GL11.GL_LINES);
	}

	public BoundingBoxRenderer(SceneObject root, ICamera cam) {
		super(root, cam);
	}

	private Map<SceneObject, VAORenderable> renderables = new HashMap<>();

	private VAORenderable getRenderableFor(SceneObject obj) {
		VAORenderable renderable = renderables.get(obj);
		if (renderable == null) {

			Vector3f[] corners = obj.getBoundingBox().getLocalCorners();

			float[] data = new float[corners.length * 3];
			int index = 0;
			for (Vector3f corner : corners) {
				data[index++] = corner.x;
				data[index++] = corner.y;
				data[index++] = corner.z;
			}

			final int[] indexData = {0, 1, 1, 3, 3, 2, 2, 0,
				0, 4, 1, 5, 2, 6, 3, 7,
				4, 5, 5, 7, 7, 6, 6, 4};

			renderable = new VAORenderable(data, indexData, new DataFragment[]{DataFragment.POSITION}, obj.getBoundingBox());
			renderables.put(obj, renderable);
		}
		return renderable;
	}

	@Override
	public void renderSelf(SceneObject object, Matrix4f transform) {
		shader.use();
		
		final int modelUniform = shader.getUniform(ModelMatrixUniformName, false);
		if (modelUniform >= 0) {
			//only if shader wants the model matrix
			final Matrix4f model = transform;
			final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
			GL20.glUniformMatrix4fv(modelUniform, false, model.get(buffer));
			GLErrors.checkForError(TAG, "glUniformMatrix4fv");
		}
		Vector3f boxColor = object.getBoundingBox().getColor();
		GL20.glUniform3f(shader.getUniform("boxColor"), boxColor.x, boxColor.y, boxColor.z);

		getRenderableFor(object).render(shader);
	}
}
