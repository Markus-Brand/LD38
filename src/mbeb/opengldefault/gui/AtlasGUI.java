package mbeb.opengldefault.gui;

import mbeb.opengldefault.gl.texture.Texture2D;
import org.joml.Vector2f;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gui.elements.AtlasGUIElement;
import mbeb.opengldefault.gl.shader.ShaderProgram;

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
	private Texture2D atlas;

	/**
	 * Width and height of the texture Atlas
	 */
	protected int atlasWidth, atlasHeight;

	public AtlasGUI(String atlasName, int atlasWidth, int atlasHeight) {
		this(new Texture2D(atlasName), atlasWidth, atlasHeight);
	}

	public AtlasGUI(Texture2D atlas, int atlasWidth, int atlasHeight) {
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
		AtlasGUIElement newElement = new AtlasGUIElement(atlasIndex, atlasWidth, atlasHeight, size);
		return (AtlasGUIElement)addGUIElement(newElement);
	}

	@Override
	public void render(ShaderProgram shader) {
		this.atlas.bind();
		shader.setUniform("u_texture", this.atlas);
		super.render(shader);
	}

}
