package mbeb.opengldefault.rendering.renderable;

import org.joml.*;

import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.scene.*;

/**
 * Provides static Meshes
 *
 * @author Markus
 */
public class StaticMeshes {

	private static VAORenderable screenAlignedQuad;
	private static VAORenderable guiQuad;
	private static VAORenderable uniformCube;
	private static VAORenderable lineCube;

	private StaticMeshes() {
		//should never be instantiated
	}

	/**
	 * Get A Renderable containing vertexData of a ScreenAlignedQuad
	 *
	 * @return ScreenAlignedQuad Renderable
	 */
	public static VAORenderable getScreenAlignedQuad() {
		if (screenAlignedQuad == null) {

			final float[] vertexData = {-1, -1, 1, -1, -1, 1, 1, 1};

			final int[] indexData = {0, 1, 2, 1, 3, 2};

			screenAlignedQuad =
					new VAORenderable(vertexData, indexData, new DataFragment[] {DataFragment.POSITION2D},
							new BoundingBox(new Vector3f(-1, -1, 0), new Vector3f(2, 2, 0)));
		}
		return screenAlignedQuad;
	}

	/**
	 * Get A Renderable containing vertexData of a ScreenAlignedQuad
	 *
	 * @return ScreenAlignedQuad Renderable
	 */
	public static VAORenderable getGuiQuad() {
		if (guiQuad == null) {
			guiQuad = getNewGuiQuad();
		}
		return guiQuad;
	}

	public static VAORenderable getNewGuiQuad() {
		final float[] vertexData = {
				0, 0, 0, 0,
				1, 0, 1, 0,
				0, 1, 0, 1,
				1, 1, 1, 1};

		final int[] indexData = {0, 1, 2, 1, 3, 2};

		return new VAORenderable(vertexData, indexData, new DataFragment[] {DataFragment.POSITION2D,
				DataFragment.UV}, new BoundingBox(new Vector3f(-1, -1, 0), new Vector3f(2, 2, 0)));
	}

	/**
	 * Gets A Renderable containing vertexData of a uniform Cube. Used in {@link Skybox}
	 *
	 * @return Cube Renderable
	 */
	public static VAORenderable getCube() {
		if (uniformCube == null) {

			/* @formatter:off */
			final float[] vertexData = {
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

			final int[] indexData = {
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

			uniformCube =
					new VAORenderable(vertexData, indexData, new DataFragment[] {DataFragment.POSITION},
							new BoundingBox(new Vector3f(-1), new Vector3f(2)));
		}
		return uniformCube;
	}

	/**
	 * a Cube from (0,0,0) to (1,1,1) that can be rendered with GL_LINES
	 *
	 * @return
	 */
	public static VAORenderable getLineCube() {
		if (lineCube == null) {

			BoundingBox uniformBox = new BoundingBox.Empty();
			uniformBox = uniformBox.extendTo(new Vector3f(0));
			uniformBox = uniformBox.extendTo(new Vector3f(1));

			Vector3f[] corners = uniformBox.getLocalCorners();

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

			lineCube = new VAORenderable(data, indexData, new DataFragment[] {DataFragment.POSITION}, uniformBox);
		}
		return lineCube;
	}
}
