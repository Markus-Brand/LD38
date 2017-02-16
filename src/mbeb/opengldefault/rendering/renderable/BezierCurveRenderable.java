package mbeb.opengldefault.rendering.renderable;

import java.nio.*;

import mbeb.opengldefault.curves.*;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

/**
 * Renders a {@link BezierCurve}
 *
 * @author Markus
 */
public class BezierCurveRenderable implements IRenderable {

	private static final String TAG = "BezierCurveRenderable";
	private final BezierCurve curve;
	private final VAORenderable renderable;

	public BezierCurveRenderable(final BezierCurve curve) {
		this.curve = curve;
		final float[] vertexData = {0, 1};

		final int[] indexData = {0, 1};

		final BoundingBox bb = generateBoundingBox();

		renderable = new VAORenderable(vertexData, indexData, new DataFragment[] {DataFragment.FLOAT}, bb);
	}

	/**
	 * Generates the Bounding Box for the curve by calculating the min and max of all controlPoints.
	 *
	 * @return the calculated 3D AABB
	 */
	private BoundingBox generateBoundingBox() {
		final Vector3f curveStart = curve.getControlPoints().get(0);
		float minX = curveStart.x, minY = curveStart.y, minZ = curveStart.z, maxX = curveStart.x, maxY = curveStart.y, maxZ = curveStart.z;

		for (final Vector3f curvePoint : curve.getControlPoints()) {
			minX = java.lang.Math.min(minX, curvePoint.x);
			minY = java.lang.Math.min(minY, curvePoint.y);
			minZ = java.lang.Math.min(minZ, curvePoint.z);

			maxX = java.lang.Math.max(maxX, curvePoint.x);
			maxY = java.lang.Math.max(maxY, curvePoint.y);
			maxZ = java.lang.Math.max(maxZ, curvePoint.z);
		}
		return new BoundingBox(new Vector3f(minX, minY, minZ), new Vector3f(maxX - minX, maxY - minY, maxZ - minZ));
	}

	@Override
	public void render(final Shader shader) {
		shader.use();
		GL11.glLineWidth(3);

		int modelUniform = shader.getUniform("bernstein");

		final FloatBuffer bernsteinBuffer = BufferUtils.createFloatBuffer(16);
		GL20.glUniformMatrix4fv(modelUniform, false, curve.bernstein().get(bernsteinBuffer));
		GLErrors.checkForError(TAG, "glUniformMatrix4fv");

		for (final Matrix4f bezierMatrix : curve.getBezierMatrices()) {
			modelUniform = shader.getUniform("bezier");

			final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
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
