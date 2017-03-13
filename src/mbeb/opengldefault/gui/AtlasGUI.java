package mbeb.opengldefault.gui;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.rendering.textures.Texture;

/**
 * A {@link GUI} subclass that renders {@link AtlasGUIElement}s from a Texture Atlas in one drawcall
 *
 * @author Markus
 */
public class AtlasGUI extends GUI {
	/**
	 * The texture atlas
	 */
	private Texture atlas;

	private static final int FLOAT_SIZE = 4;
	private static final int VEC4_SIZE = FLOAT_SIZE * 4;
	private static final int MAT4_SIZE = VEC4_SIZE * 4;

	public AtlasGUI(int atlasSize, String atlasName) {
		super();
		atlas = new Texture(atlasName);
		stride = 5 * VEC4_SIZE;
	}

	@Override
	public void setupVAO() {
		super.setupVAO();
		renderable.bind();
		glEnableVertexAttribArray(7);
		glVertexAttribPointer(7, 4, GL_FLOAT, false, stride, 4 * VEC4_SIZE);

		glVertexAttribDivisor(7, 1);
		renderable.unbind();
	}

	@Override
	public void render(Shader shader) {
		atlas.bind(shader);
		super.render(shader);
	}

}
