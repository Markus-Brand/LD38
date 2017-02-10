package mbeb.opengldefault.rendering.renderable;


public class ScreenAlignedQuad {
	private static final String TAG = "ScreenAlignedQuad";

	private static IRenderable renderable;

	public static IRenderable getRenderable() {
		if (renderable == null) {

			float[] vertexData = { -1, -1, 1, -1, -1, 1, 1, 1 };

			int[] indexData = { 0, 1, 2, 1, 3, 2 };

			int[] dataSizes = { 2 };

			renderable = new VAORenderable(vertexData, indexData, dataSizes);

		}
		return renderable;
	}

	public static void render() {
		getRenderable().render();
	}

}
