package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.scene.BoundingBox;
import org.joml.Vector3f;


public class ScreenAlignedQuad {
	private static final String TAG = "ScreenAlignedQuad";

	private static IRenderable renderable;

	public static IRenderable getRenderable() {
		if (renderable == null) {

			float[] vertexData = { -1, -1, 1, -1, -1, 1, 1, 1 };

			int[] indexData = { 0, 1, 2, 1, 3, 2 };

			int[] dataSizes = { 2 };

			renderable = new VAORenderable(vertexData, indexData, dataSizes, new BoundingBox(new Vector3f(-1, -1, 0), new Vector3f(2, 2, 0)));

		}
		return renderable;
	}

	public static void render(Shader shader) {
		getRenderable().render(shader);
	}

}
