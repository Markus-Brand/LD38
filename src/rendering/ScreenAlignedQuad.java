package rendering;

public class ScreenAlignedQuad {
	private static final String TAG = "ScreenAlignedQuad";

	private static Renderable renderable;

	public static Renderable getRenderable() {
		if (renderable == null) {

			float[] vertexData = { -1, -1, 1, -1, -1, 1, 1, 1 };

			int[] indexData = { 0, 1, 2, 1, 3, 2 };

			int[] dataSizes = { 2 };

			renderable = new Renderable(vertexData, indexData, dataSizes);

		}
		return renderable;
	}

	public static void render() {
		getRenderable().bind();
		getRenderable().render();
		getRenderable().unbind();
	}

}
