package mbeb.opengldefault.gui;

import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * A CircleBar {@link GUIElement}, like the Stamina Meter in Zelda BotW
 * 
 * @author Markus
 */
public class CircleBar extends GUIElement {
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
	public void writeToBuffer(FloatBuffer buffer, int offset) {
		getModelMatrix().get(offset, buffer);
		int offsetByMatrix = 16;
		new Vector4f(progress).get(offset + offsetByMatrix, buffer);
	}

}
