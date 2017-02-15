package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.scene.*;

import org.joml.*;

/**
 * Provides static Meshes
 * 
 * @author Markus
 */
public class StaticMeshes {

	private static IRenderable screenAlignedQuad;
	private static IRenderable uniformCube;

	/**
	 * Get A Renderable containing vertexData of a ScreenAlignedQuad
	 *
	 * @return ScreenAlignedQuad Renderable
	 */
	public static IRenderable getScreenAlignedQuad() {
		if (screenAlignedQuad == null) {

			float[] vertexData = {-1, -1, 1, -1, -1, 1, 1, 1};

			int[] indexData = {0, 1, 2, 1, 3, 2};

			int[] dataSizes = {2};

			screenAlignedQuad = new VAORenderable(vertexData, indexData, dataSizes, new BoundingBox(new Vector3f(-1, -1, 0), new Vector3f(2, 2, 0)));

		}
		return screenAlignedQuad;
	}

	/**
	 * Get A Renderable containing vertexData of a uniform Cube. Used in {@link Skybox}
	 *
	 * @return Cube Renderable
	 */
	public static IRenderable getCube() {
		if (uniformCube == null) {

			/* @formatter:off */
			float[] vertexData = {
					// front
					-1.0f, -1.0f,  1.0f,
					 1.0f, -1.0f,  1.0f,
					 1.0f,  1.0f,  1.0f,
					-1.0f,  1.0f,  1.0f,
					// back
					-1.0f, -1.0f, -1.0f,
					 1.0f, -1.0f, -1.0f,
					 1.0f,  1.0f, -1.0f,
					-1.0f,  1.0f, -1.0f,};

			int[] indexData = {
					// front
					0, 2, 1, 2, 0, 3,
					// top
					5, 1, 6, 6, 1, 2,
					// back
					6, 7, 5, 4, 5, 7,
					// bottom
					0, 4, 3, 3, 4, 7,
					// left
					4, 1, 5, 0, 1, 4,
					// right
					2, 3, 6, 6, 3, 7};

			/* @formatter:on */
			int[] dataSizes = {3};

			uniformCube = new VAORenderable(vertexData, indexData, dataSizes, new BoundingBox(new Vector3f(-1, -1, -1), new Vector3f(2, 2, 2)));

		}
		return uniformCube;
	}
}
