package mbeb.opengldefault.gui;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import org.joml.Vector2f;

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

	/**
	 * Width and height of the texture Atlas
	 */
	private int atlasWidth, atlasHeight;

	public AtlasGUI(String atlasName, int atlasWidth, int atlasHeight) {
		super();
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		atlas = new Texture(atlasName);
		//Store a Matrix and the offset Vector from {@link AtlasGUIElement}
		stride = MAT4_SIZE + VEC4_SIZE;
	}

	/**
	 * Adds a {@link AtlasGUIElement} to the GUI
	 * 
	 * @param atlasIndex
	 *            index in the Atlas {@link AtlasGUIElement#atlasIndex}
	 * @param position
	 *            position of the element
	 * @param size
	 *            size of the element
	 * @return the generated and added element
	 */
	public AtlasGUIElement addAtlasGUI(int atlasIndex, Vector2f position, Vector2f size) {
		AtlasGUIElement newElement = new AtlasGUIElement(atlasIndex, atlasWidth, atlasHeight, size);
		elements.add(newElement);
		return newElement;
	}

	/**
	 * Adds a {@link TextGUIElement} to the GUI
	 * 
	 * @param text
	 *            the displayed text
	 * @param position
	 *            position of the element
	 * @param width
	 *            width of a letter
	 * @return
	 */
	public TextGUIElement addText(String text, Vector2f position, float width) {
		TextGUIElement newElement = new TextGUIElement(atlasWidth, atlasHeight, text, position, width);
		elements.add(newElement);
		return newElement;
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
