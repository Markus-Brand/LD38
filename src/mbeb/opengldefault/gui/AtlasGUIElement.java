package mbeb.opengldefault.gui;

import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * A {@link GUIElement} that gets rendered by a {@link AtlasGUI}
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
	 * Size of the texture atlas
	 */
	private int atlasSize;

	public AtlasGUIElement(int atlasIndex, int atlasSize, Vector2f position, Vector2f size) {
		super(position, size);
		this.atlasIndex = atlasIndex;
		this.atlasSize = atlasSize;
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
	 * Calculates a Vector4f that can be sent to the GPU containing the atlasIndex, the atlasSIze, and the x and y
	 * offset in the atlas
	 *
	 * @param atlasSize
	 * @return
	 */
	public Vector4f getOffset(int atlasSize) {
		return new Vector4f(atlasIndex, atlasSize, atlasIndex % atlasSize / (float) atlasSize, atlasIndex / atlasSize
				/ (float) atlasSize);
	}

	@Override
	public void writeToBuffer(FloatBuffer buffer, int offset) {
		getModelMatrix().get(offset, buffer);
		getOffset(atlasSize).get(offset + 16, buffer);
	}

	@Override
	public void update(double deltaTime) {

	}
}
