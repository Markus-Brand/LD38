package mbeb.opengldefault.gui;

import mbeb.opengldefault.openglcontext.OpenGLContext;

import org.joml.Vector2f;

/**
 * A {@link CombinedGUIElement} that is used to draw a String by using a {@link AtlasGUI}
 * 
 * @author Markus
 */
public class TextGUIElement extends CombinedGUIElement {

	/**
	 * Width and height of the Atlas
	 */
	private int atlasWidth, atlasHeight;

	/**
	 * Text that will be rendered
	 */
	private String text;

	/**
	 * Width of one character
	 */
	private float width;

	public TextGUIElement(int atlasWidth, int atlasHeight, String text, Vector2f position, float width) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.text = text;
		this.width = width;
		setBounding(new Rectangle(position, new Vector2f()));
		generateText();
	}

	/**
	 * Setter for the text
	 *
	 * @param text
	 *            new text
	 */
	public void setText(String text) {
		this.text = text;
		generateText();
	}

	@Override
	public void setPosition(Vector2f position) {
		super.setPosition(position);
		generateText();
	}

	/**
	 * Setter for one characters width
	 *
	 * @param width
	 *            new width for one character
	 */
	public void setWidth(float width) {
		this.width = width;
		generateText();
	}

	/**
	 * Generates the Text by adding AtlasGUIElements into the List of elements for every character in the String
	 *
	 * @return
	 */
	public CombinedGUIElement generateText() {
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
