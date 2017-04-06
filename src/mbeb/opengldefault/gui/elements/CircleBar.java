package mbeb.opengldefault.gui.elements;

import java.nio.FloatBuffer;

import mbeb.opengldefault.constants.Constants;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * A CircleBar {@link GUIElement}, like the Stamina Meter in Zelda BotW
 *
 * @author Markus
 */
public class CircleBar extends GUIElement {
	/**
	 * Progress of the bar. 0-1 to fill the bar and 1-2 for the color to get white
	 */
	float progress;

	public CircleBar(Vector2f position, Vector2f size) {
		super(position, size);
		progress = 0;
	}

	public CircleBar(Vector2f size) {
		this(new Vector2f(), size);
	}

	public CircleBar() {
		this(new Vector2f(1));
	}

	@Override
	public void update(double deltaTime) {
		progress += deltaTime * 0.1;
		progress %= 2;
		setDirty();
	}

	@Override
	public int writeToBuffer(FloatBuffer buffer, int offset) {
		int offsetBySuper = super.writeToBuffer(buffer, offset);
		new Vector4f(progress).get(offset + offsetBySuper, buffer);
		return offsetBySuper + Constants.VEC4_COMPONENTS;
	}

}
