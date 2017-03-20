package mbeb.opengldefault.gui;

import mbeb.opengldefault.openglcontext.OpenGLContext;

import org.joml.Vector2f;

public class TextGUIElement extends CombinedGUIElement {

	private int atlasWidth, atlasHeight;

	private String text;

	private float width;

	public TextGUIElement(int atlasWidth, int atlasHeight, String text, Vector2f position, float width) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.text = text;
		this.width = width;
		setBounding(new Rectangle(position, new Vector2f()));
		regenerate();
	}

	public void setText(String text) {
		this.text = text;
		regenerate();
	}

	@Override
	public void setPosition(Vector2f position) {
		super.setPosition(position);
		regenerate();
	}

	public void setWidth(float width) {
		this.width = width;
		regenerate();
	}

	public CombinedGUIElement regenerate() {
		Vector2f textPos = getPosition();
		resetElements();
		float xPos = textPos.x;
		for (char c : text.toCharArray()) {
			addGUIElement(new AtlasGUIElement(c - 32, atlasWidth, atlasHeight, new Vector2f(xPos, textPos.y),
					new Vector2f(
							width,
							atlasWidth / (float) atlasHeight * width * OpenGLContext.getAspectRatio())));
			xPos += width;
		}
		return this;
	}

}
