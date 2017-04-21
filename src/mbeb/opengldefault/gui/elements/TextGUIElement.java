package mbeb.opengldefault.gui.elements;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.shapes.Rectangle;

import org.joml.Vector2f;

/**
 * A {@link CombinedGUIElement} that is used to draw a String by using an {@link AtlasGUI}
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
		height = font.getSize() / (float) GLContext.getFramebufferHeight();
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
		setDirty();
	}

	@Override
	public GUIElement setPositionRelativeTo(Rectangle bounding, float relativeX, float relativeY) {
		checkDirty();
		return super.setPositionRelativeTo(bounding, relativeX, relativeY);
	}

	/**
	 * Setter for one characters width
	 *
	 * @param width
	 *            new width for one character
	 */
	public void setHeight(float height) {
		this.height = height;
		setDirty();
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
			float charWidth = font.stringWidth("" + c) / (float) GLContext.getFramebufferWidth();
			addGUIElement(new AtlasGUIElement(c, atlasWidth, atlasHeight, new Vector2f(xPos, textPos.y),
					new Vector2f(
							height / GLContext.getAspectRatio(),
							height), getLutRow(), getLut()));
			xPos += charWidth;
		}
		setSize(new Vector2f(xPos - textPos.x, getSize().y));
		setClean();
		return this;
	}

	/**
	 * Getter for the FontMetrics
	 *
	 * @return the fonts FontMetrics
	 */
	public FontMetrics getFont() {
		return font;
	}

	@Override
	public void writeTo(GLBufferWriter writer) {
		checkDirty();
		super.writeTo(writer);
	}

	@Override
	public int getNumElements() {
		checkDirty();
		return super.getNumElements();
	}

	/**
	 * Checks if the element is dirty and newly generates the text, if that is the case
	 */
	private void checkDirty() {
		if (isDirty()) {
			generateText();
		}
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
