package mbeb.opengldefault.gui;

import mbeb.opengldefault.openglcontext.OpenGLContext;

import org.joml.Vector2f;

public class TextGUI extends AtlasGUI {

	int atlasWidth, atlasHeight;

	public TextGUI(int atlasWidth, int atlasHeight, String atlasName) {
		super(atlasName);
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
	}

	public CombinedGUIElement addTextElement(String text, Vector2f position, float width) {
		CombinedGUIElement combined = new CombinedGUIElement();
		float xPos = position.x;
		for (char c : text.toCharArray()) {
			combined.addGUIElement(new AtlasGUIElement(c - 32, atlasWidth, atlasHeight, new Vector2f(xPos, position.y),
					new Vector2f(
							width,
							atlasWidth / (float) atlasHeight * width * OpenGLContext.getAspectRatio())));
			xPos += width;
		}
		super.addGUIElement(combined);
		return combined;
	}

}
