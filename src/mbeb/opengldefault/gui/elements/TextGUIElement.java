package mbeb.opengldefault.gui.elements;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.shapes.Rectangle;

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
	 * FontMetrics used for retrieving the size of a String that will be rendered
	 */
	private FontMetrics font;

	/**
	 * Width of one character
	 */
	private float height;

	public TextGUIElement(int atlasWidth, int atlasHeight, String text, Vector2f position, Font font) {
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.text = text;
		height = font.getSize() / (float) OpenGLContext.getFramebufferHeight();
		setFont(font);
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
	public void setHeight(float height) {
		this.height = height;
		generateText();
	}

	/**
	 * Generates the Text by adding AtlasGUIElements into the List of elements for every character in the String
	 *
	 * @return this
	 */
	public TextGUIElement generateText() {
		Vector2f textPos = getPosition();
		resetElements();
		float xPos = textPos.x;
		for (char c : text.toCharArray()) {
			float charWidth = font.stringWidth("" + c) / (float) OpenGLContext.getFramebufferWidth();
			addGUIElement(new AtlasGUIElement(c, atlasWidth, atlasHeight, new Vector2f(xPos, textPos.y),
					new Vector2f(
							height / OpenGLContext.getAspectRatio(),
							height), getLutRow(), getLut()));
			xPos += charWidth;
		}
		setSize(new Vector2f(xPos - textPos.x, getSize().y));
		return this;
	}

	/**
	 * Getter fot the FontMetrics
	 * 
	 * @return the fonts FontMetrics
	 */
	public FontMetrics getFont() {
		return font;
	}

	/**
	 * @param font
	 *            the font that will be converted to the FontMetrics
	 */
	public void setFont(Font font) {
		Graphics2D g2d = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
		this.font = g2d.getFontMetrics(font);
	}

}
