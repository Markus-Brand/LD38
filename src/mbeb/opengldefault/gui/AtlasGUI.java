package mbeb.opengldefault.gui;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gui.elements.AtlasGUIElement;
import mbeb.opengldefault.rendering.shader.ShaderProgram;
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
	protected int atlasWidth, atlasHeight;

	public AtlasGUI(String atlasName, int atlasWidth, int atlasHeight) {
		this(new Texture(atlasName), atlasWidth, atlasHeight);
	}

	public AtlasGUI(Texture atlas, int atlasWidth, int atlasHeight) {
		super();
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.atlas = atlas;
		//Store a Matrix, the offset Vector from {@link AtlasGUIElement} and the lut Vector
		stride = Constants.MAT4_COMPONENTS + Constants.VEC4_COMPONENTS + Constants.VEC4_COMPONENTS;
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
	public AtlasGUIElement addAtlasGUIElement(int atlasIndex, Vector2f position, Vector2f size) {
		AtlasGUIElement newElement = new AtlasGUIElement(atlasIndex, atlasWidth, atlasHeight, position, size);
		return (AtlasGUIElement)addGUIElement(newElement);
	}

	@Override
	public void render(ShaderProgram shader) {
		atlas.bind(shader);
		super.render(shader);
	}

}
