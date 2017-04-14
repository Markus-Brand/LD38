package mbeb.opengldefault.gl.texture;

import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import mbeb.opengldefault.logging.GLErrors;

/**
 * Represents an 3D texture.
 */
public class Texture3D extends ThreeDimensionalTexture {
	private static final String TAG = "Texture3D";

	/**
	 * Creates a new texture.
	 * The created texture is not generated yet.
	 */
	public Texture3D() {
		super(Type.TEXTURE_3D);
	}

	/**
	 * Creates an uninitialized 3D texture of the given dimensions and format.
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
	public Texture3D(int width, int height, int depth, InternalFormat format) {
		this();
		this.setData(format, width, height, depth, format.getMinimalData(), DataType.UNSIGNED_BYTE, null);
	}

	/**
	 * Creates a 3D texture with the given images.
	 *
	 * @param images
	 *            the images to use
	 */
	public Texture3D(BufferedImage[] images) {
		this(images[0].getWidth(), images[0].getHeight(), images.length, InternalFormat.RGBA8);
		this.setLayers(images);
	}
}
