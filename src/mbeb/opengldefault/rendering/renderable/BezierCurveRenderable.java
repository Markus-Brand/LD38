package mbeb.opengldefault.rendering.renderable;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.scene.BoundingBox;

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

		renderable = new VAORenderable(StaticMeshes.getBezierData());
		renderable.setBoundingBox(generateBoundingBox());
	}

	/**
	 * Generates the Bounding Box for the curve by calculating the min and max of all controlPoints.
	 *
	 * @return the calculated 3D AABB
	 */
	private BoundingBox generateBoundingBox() {
		final Vector3f curveStart = curve.getControlPoints().get(0);
		float minX = curveStart.x, minY = curveStart.y, minZ = curveStart.z;
		float maxX = curveStart.x, maxY = curveStart.y, maxZ = curveStart.z;

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
	public void render(final ShaderProgram shader) {
		shader.use();
		float[] widthRange = new float[2];
		GL11.glGetFloatv(GL11.GL_LINE_WIDTH_RANGE, widthRange);
		GLErrors.checkForError(TAG, "glGetFloatv");
		GL11.glLineWidth(Float.min(widthRange[1], 3.0f));
		GLErrors.checkForError(TAG, "glLineWidth");

		shader.setUniform("bernstein", curve.bernstein());

		for (final Matrix4f bezierMatrix : curve.getBezierMatrices()) {
			shader.setUniform("bezier", bezierMatrix);

			renderable.render(shader);
		}
		GL11.glLineWidth(1);
		GLErrors.checkForError(TAG, "glLineWidth");
	}

	@Override
	public BoundingBox getBoundingBox() {
		return renderable.getBoundingBox();
	}

	public BezierCurve getCurve() {
		return curve;
	}
}
