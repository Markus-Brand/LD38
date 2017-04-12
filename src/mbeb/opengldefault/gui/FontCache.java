package mbeb.opengldefault.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.gui.elements.TextGUIElement;

/**
 * A class used for generating FontMap {@link Texture2D}s that can be used to render Strings using a
 * {@link TextGUIElement}
 * 
 * @author Markus
 */
public class FontCache {
	/**
	 * Map containing a mapping from {@link Font} to Texture representing the FontCache
	 */
	private static Map<Font, Texture2D> cachedFonts;

	static {
		cachedFonts = new HashMap<>();
	}

	/**
	 * The static method that is used to access the FontMaps
	 * 
	 * @param font
	 *            The font, that will be used to generate the FontMap, if it isn't already loaded
	 * @return the generated or cached FontMap {@link Texture2D}
	 */
	public static Texture2D getFont(Font font) {
		Font resizedFont = getResizedFont(font, font.getSize());
		if (!cachedFonts.containsKey(resizedFont)) {
			addFont(resizedFont);
		}
		return cachedFonts.get(resizedFont);
	}

	/**
	 * Generates the {@link Texture2D} and adds it to the cache
	 * 
	 * @param resizedFont
	 *            the {@link Font} used for generating the FontMap {@link Texture2D}
	 */
	private static void addFont(Font resizedFont) {
		System.out.println("Load font: " + resizedFont.getName() + " Size " + resizedFont.getSize());

		BufferedImage rasteredFont =
				new BufferedImage(resizedFont.getSize() * 32, resizedFont.getSize() * 16, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = setupGraphics(resizedFont, rasteredFont);

		int yOffset = g2d.getFontMetrics(resizedFont).getAscent() - g2d.getFontMetrics(resizedFont).getDescent();

		drawChars(g2d, resizedFont.getSize(), yOffset);

		g2d.dispose();
		Texture2D font = new Texture2D(rasteredFont);
		font.whileBound(texture -> font.setWrapMode(Texture.WrapMode.CLAMP_TO_EDGE) && font.setInterpolates(false));
		cachedFonts.put(resizedFont, font);
	}

	/**
	 * Generates a {@link Graphics2D} object for rendering the FontMap into the texture
	 * @param resizedFont the {@link Font} that will be used
	 * @param rasteredFont the {@link BufferedImage} that will be used
	 * @return the new {@link Graphics2D} object
	 */
	private static Graphics2D setupGraphics(Font resizedFont, BufferedImage rasteredFont) {
		Graphics2D g2d = rasteredFont.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(resizedFont);
		g2d.setColor(Color.WHITE);
		return g2d;
	}

	/**
	 * Iterates over the first 512 chars and draws them into the FontMap
	 * 
	 * @param g2d
	 *            the graphics context that is used to render the chars
	 * @param size
	 *            size of the space that is reserved for each char in pixels
	 * @param yOffset
	 *            offset for the char to be rendered centered in the y direction (due to baseline relative y coordinates
	 *            with drawString)
	 */
	private static void drawChars(Graphics2D g2d, int size, int yOffset) {
		for (int i = 0; i < 512; i++) {
			int xPos = i % 32 * size;
			int yPos = i / 32 * size;
			g2d.setClip(xPos, yPos, size, size);
			g2d.drawString("" + (char) i, xPos, yPos + yOffset);
		}
	}

	/**
	 * converts the {@link Font} to a {@link Font} with a power of two size
	 * 
	 * @param font
	 *            input {@link Font}
	 * @param minimumCharHeight
	 *            height of a char in the input {@link Font}
	 * @return the resized {@link Font}
	 */
	private static Font getResizedFont(Font font, int minimumCharHeight) {
		int size = 2;
		while(size < minimumCharHeight) {
			size <<= 1;
		}
		return font.deriveFont((float) size);
	}
}
