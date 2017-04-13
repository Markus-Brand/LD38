package mbeb.opengldefault.gui.elements;


import mbeb.opengldefault.gl.buffer.GLBufferWriter;
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
	public void writeTo(GLBufferWriter writer) {
		super.writeTo(writer);
		writer.write(new Vector4f(progress));
	}
}
