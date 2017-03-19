package mbeb.opengldefault.gui;

import java.nio.FloatBuffer;

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
	 * 6|7|8
	 * -----
	 * 3|4|5
	 * -----
	 * 0|1|2
	 */
	private int atlasIndex;

	/**
	 * Width of the texture atlas
	 */
	private int atlasWidth;
	/**
	 * Height of the texture atlas
	 */
	private int atlasHeight;

	public AtlasGUIElement(int atlasIndex, int atlasWidth, int atlasHeight, Vector2f position, Vector2f size) {
		super(position, size);
		this.atlasIndex = atlasIndex;
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
	}

	public AtlasGUIElement(int atlasIndex, int atlasSize, Vector2f position, Vector2f size) {
		super(position, size);
		this.atlasIndex = atlasIndex;
		this.atlasWidth = atlasSize;
		this.atlasHeight = atlasSize;
	}

	public AtlasGUIElement(int atlasIndex, int atlasSize) {
		this(atlasIndex, atlasSize, new Vector2f(1));
	}

	public AtlasGUIElement(int atlasIndex, int atlasSize, Vector2f size) {
		this(atlasIndex, atlasSize, new Vector2f(), size);
	}

	public AtlasGUIElement(Vector2f size) {
		this(0, 1, size);
	}

	public AtlasGUIElement() {
		this(0, 1);
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
		getModelMatrix().get(offset, buffer);
		int offsetByMatrix = 16;
		getOffset().get(offset + offsetByMatrix, buffer);
		return 20;
	}

	@Override
	public void update(double deltaTime) {

	}
}
