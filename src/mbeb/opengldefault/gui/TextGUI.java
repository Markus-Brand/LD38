package mbeb.opengldefault.gui;

import java.awt.Font;

import mbeb.opengldefault.gui.elements.TextGUIElement;
import mbeb.opengldefault.openglcontext.OpenGLContext;

import org.joml.Vector2f;

public class TextGUI extends AtlasGUI {

	private Font font;

	public TextGUI(Font font) {
		super(FontCache.getFont(font), 32, 16);
		setFont(font);
	}

	/**
	 * Adds a {@link TextGUIElement} to the GUI
	 *
	 * @param text
	 *            the displayed text
	 * @param position
	 *            position of the element
	 * @param width
	 *            width of a letter
	 * @return
	 */
	public TextGUIElement addText(String text, Vector2f position, float height) {
		TextGUIElement newElement = new TextGUIElement(atlasWidth, atlasHeight, text, position, getFont(height));
		newElement.setLut(getLut(), elements.size() % 256 / 255f);
		elements.add(newElement);
		return newElement;
	}

	/**
	 * Adds a {@link TextGUIElement} to the GUI
	 *
	 * @param text
	 *            the displayed text
	 * @param position
	 *            position of the element
	 * @param width
	 *            width of a letter
	 * @return
	 */
	public TextGUIElement addText(String text, Vector2f position) {
		TextGUIElement newElement = new TextGUIElement(atlasWidth, atlasHeight, text, position, getFont());
		elements.add(newElement);
		return newElement;
	}

	/**
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Copys the font and sets a new size
	 *
	 * @param size
	 *            the new font size
	 * @return new Font with given size
	 */
	private Font getFont(float size) {
		return font.deriveFont(size * OpenGLContext.getFramebufferHeight());
	}

	/**
	 * @param font
	 *            the font to set
	 */
	public void setFont(Font font) {
		this.font = font;
	}
}
