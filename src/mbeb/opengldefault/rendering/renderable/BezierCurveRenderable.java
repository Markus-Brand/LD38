package mbeb.opengldefault.rendering.renderable;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * Renders a {@link BezierCurve}
 * 
 * @author Markus
 */
public class BezierCurveRenderable implements IRenderable {

	private static final String TAG = "BezierCurveRenderable";
	private BezierCurve curve;
	private VAORenderable renderable;

	public BezierCurveRenderable(BezierCurve curve) {
		this.curve = curve;
		float[] vertexData = {0, 1};

		int[] indexData = {0, 1};

		int[] dataSizes = {1};

		BoundingBox bb = generateBoundingBox();

		renderable = new VAORenderable(vertexData, indexData, dataSizes, bb);
	}

	/**
	 * Generates the Bounding Box for the curve by calculating the min and max off all controlPoints.
	 *
	 * @return the calculated 3D AABB
	 */
	private BoundingBox generateBoundingBox() {
		Vector3f curveStart = curve.getControlPoints().get(0);
		float minX = curveStart.x, minY = curveStart.y, minZ = curveStart.z, maxX = curveStart.x, maxY = curveStart.y, maxZ =
				curveStart.z;

		for (Vector3f curvePoint : curve.getControlPoints()) {
			minX = Math.min(minX, curvePoint.x);
			minY = Math.min(minY, curvePoint.y);
			minZ = Math.min(minZ, curvePoint.z);

			maxX = Math.max(maxX, curvePoint.x);
			maxY = Math.max(maxY, curvePoint.y);
			maxZ = Math.max(maxZ, curvePoint.z);
		}
		BoundingBox bb =
				new BoundingBox(new Vector3f(minX, minY, minZ), new Vector3f(maxX - minX, maxY - minY, maxZ - minZ));
		return bb;
	}

	@Override
	public void render(Shader shader) {
		shader.use();
		GL11.glLineWidth(3);

		int modelUniform = shader.getUniform("bernstein");

		FloatBuffer bernsteinBuffer = BufferUtils.createFloatBuffer(16);
		GL20.glUniformMatrix4fv(modelUniform, false, curve.bernstein().get(bernsteinBuffer));
		GLErrors.checkForError(TAG, "glUniformMatrix4fv");

		for (Matrix4f bezierMatrix : curve.getBezierMatrices()) {
			modelUniform = shader.getUniform("bezier");

			FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
			GL20.glUniformMatrix4fv(modelUniform, false, bezierMatrix.get(buffer));
			GLErrors.checkForError(TAG, "glUniformMatrix4fv");

			renderable.render(shader);
		}
		GL11.glLineWidth(1);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return renderable.getBoundingBox();
	}
}
