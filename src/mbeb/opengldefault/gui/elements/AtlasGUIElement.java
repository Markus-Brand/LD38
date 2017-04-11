package mbeb.opengldefault.gui.elements;

import java.nio.FloatBuffer;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.rendering.textures.Texture;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * An {@link GUIElement} that gets rendered by a {@link AtlasGUI}
 *
 * @author Markus
 */
public class AtlasGUIElement extends GUIElement {

	/**
	 * Index in the atlas.
	 * Example for a 3 * 3 atlas:
	 * 0|1|2
	 * -----
	 * 3|4|5
	 * -----
	 * 6|7|8
	 */
	private int atlasIndex;

	public int getAtlasIndex() {
		return atlasIndex;
	}

	public void setAtlasIndex(int atlasIndex) {
		this.atlasIndex = atlasIndex;
		setDirty();
	}

	/**
	 * Width of the texture atlas
	 */
	private int atlasWidth;
	/**
	 * Height of the texture atlas
	 */
	private int atlasHeight;

	public AtlasGUIElement(int atlasIndex, int atlasWidth, int atlasHeight, Vector2f position, Vector2f size,
			float lutRow, Texture lut) {
		super(position, size, lutRow, lut);
		this.atlasIndex = atlasIndex;
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
	}

	public AtlasGUIElement(int atlasIndex, int atlasWidth, int atlasHeight, Vector2f position, Vector2f size) {
		super(position, size);
		this.atlasIndex = atlasIndex;
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
	}

	public AtlasGUIElement(int atlasIndex, int atlasWidth, int atlasHeight) {
		this(atlasIndex, atlasWidth, atlasHeight, new Vector2f(1));
	}

	public AtlasGUIElement(int atlasIndex, int atlasWidth, int atlasHeight, Vector2f size) {
		this(atlasIndex, atlasWidth, atlasHeight, new Vector2f(), size);
	}

	public AtlasGUIElement(Vector2f size) {
		this(0, 1, 1, size);
	}

	public AtlasGUIElement() {
		this(0, 1, 1);
	}

	/**
	 * Calculates a Vector4f that can be sent to the GPU containing the atlasIndex, the atlasSize, and the x and y
	 * offset in the atlas
	 *
	 * @param atlasSize
	 *            size of the atlas
	 * @return generated Vector
	 */
	public Vector4f getOffset() {
		float row = (atlasHeight - atlasIndex / atlasWidth - 1) / (float) atlasHeight;
		float column = atlasIndex % atlasWidth / (float) atlasWidth;
		return new Vector4f(atlasWidth, atlasHeight, column, row);
	}

	@Override
	public int writeToBuffer(FloatBuffer buffer, int offset) {
		int offsetBySuper = super.writeToBuffer(buffer, offset);
		getOffset().get(offset + offsetBySuper, buffer);
		return offsetBySuper + Constants.VEC4_COMPONENTS;
	}

	@Override
	public void update(double deltaTime) {

	}
}
