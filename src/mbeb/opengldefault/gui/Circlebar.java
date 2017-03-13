package mbeb.opengldefault.gui;

import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Circlebar extends GUIElement {
	float progress;

	public Circlebar(Vector2f position, Vector2f size) {
		super(position, size);
		progress = 0;
	}

	public Circlebar(Vector2f size) {
		this(new Vector2f(), size);
	}

	public Circlebar() {
		this(new Vector2f(1));
	}

	@Override
	public void update(double deltaTime) {
		progress += deltaTime * 0.1;
		progress %= 2;
		setDirty(true);
	}

	@Override
	public void writeToBuffer(FloatBuffer buffer, int offset) {
		getModelMatrix().get(offset, buffer);
		new Vector4f(progress).get(offset + 16, buffer);
	}

}
