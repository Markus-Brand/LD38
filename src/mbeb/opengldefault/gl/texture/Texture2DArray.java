package mbeb.opengldefault.gl.texture;

import java.awt.image.BufferedImage;

/**
 * Represents an array of 2D textures.
 * 
 * @author Potti
 */
public class Texture2DArray extends ThreeDimensionalTexture {
	private static final String TAG = "Texture2DArray";

	/**
	 * Creates a new texture.
	 * The created texture is not generated yet.
	 */
	public Texture2DArray() {
		super(Type.TEXTURE_2D_ARRAY);
	}

	/**
	 * Creates an uninitialized 2D texture array of the given dimensions and format.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the depth
	 * @param format
	 *            the format to use
	 */
	public Texture2DArray(int width, int height, int depth, InternalFormat format) {
		this();
		this.setData(format, width, height, depth, format.getMinimalData(), DataType.UNSIGNED_BYTE, null);
	}

	/**
	 * Creates a 2D texture array with the given images and depth
	 *
	 * @param images
	 *            the images to use
	 * @param depth
	 *            how many layers this texture should have
	 */
	public Texture2DArray(BufferedImage[] images, int depth) {
		this(images[0].getWidth(), images[0].getHeight(), depth, InternalFormat.RGBA8);
		this.setLayers(images);
	}

	/**
	 * Creates a 2D texture array with the given images.
	 *
	 * @param images
	 *            the images to use
	 */
	public Texture2DArray(BufferedImage[] images) {
		this(images, images.length);
	}
}
