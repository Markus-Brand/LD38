package mbeb.opengldefault.gui;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.rendering.textures.Texture;

/**
 * A {@link GUI} subclass that renders {@link AtlasGUIElement}s from a Texture Atlas in one drawcall
 *
 * @author Markus
 */
public class AtlasGUI extends GUI {
	private static final String TAG = "AtlasGUI";
	/**
	 * The texture atlas
	 */
	private Texture atlas;

	public AtlasGUI(int atlasSize, String atlasName) {
		super();
		atlas = new Texture(atlasName);
		//Store a Matrix and the offset Vector from {@link AtlasGUIElement}
		stride = MAT4_SIZE + VEC4_SIZE;
	}

	@Override
	public void setupVAO() {
		super.setupVAO();
		renderable.bind();

		glEnableVertexAttribArray(7);
		GLErrors.checkForError(TAG, "glEnableVertexAttribArray");
		glVertexAttribPointer(7, 4, GL_FLOAT, false, stride, 4 * VEC4_SIZE);
		GLErrors.checkForError(TAG, "glVertexAttribPointer");

		glVertexAttribDivisor(7, 1);
		GLErrors.checkForError(TAG, "glVertexAttribDivisor");
		renderable.unbind();
	}

	@Override
	public void render(Shader shader) {
		atlas.bind(shader);
		super.render(shader);
	}

}
