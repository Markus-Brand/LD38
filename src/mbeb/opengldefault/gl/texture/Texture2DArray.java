package mbeb.opengldefault.gl.texture;

import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import mbeb.opengldefault.logging.GLErrors;

/**
 * Represents an array of 2D textures.
 */
public class Texture2DArray extends Texture {
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
	 * Creates a 2D texture with the given images.
	 * 
	 * @param images
	 *            the images to use
	 */
	public Texture2DArray(BufferedImage[] images) {
		this(images[0].getWidth(), images[0].getHeight(), images.length, InternalFormat.RGBA8);
		this.setLayers(images);
	}

	/**
	 * Sets a single layer of this array.
	 * 
	 * @param layer
	 *            the layer to set
	 * @param image
	 *            the image to set
	 * @return whether the operation succeeded
	 */
	public boolean setLayer(int layer, BufferedImage image) {
		return this.whileBound((Texture2DArray texture) -> {
			ByteBuffer data = Texture.generateBuffer(image, true);
			return this.setLayerData(image.getWidth(), image.getHeight(), layer, Format.RGBA, DataType.UNSIGNED_BYTE, data);
		});
	}

	/**
	 * Sets all images of this texture to the given array.
	 * 
	 * @param images
	 *            the images to set
	 * @return whether the operation succeeded
	 */
	public boolean setLayers(BufferedImage[] images) {
		return this.whileBound((Texture2DArray texture) -> {
			boolean success = true;
			for (int i = 0; i < images.length && success; i++) {
				this.setLayer(i, images[i]);
			}
			return success;
		});
	}

	/**
	 * Sets data for one layer of this array.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param layer
	 *            the layer to write
	 * @param format
	 *            the format of the data
	 * @param dataType
	 *            the type of the data
	 * @param data
	 *            the data
	 * @return whether the operation succeeded
	 */
	protected boolean setLayerData(int width, int height, int layer, Format format, DataType dataType, ByteBuffer data) {
		return this.whileBound((Texture2DArray texture) -> {
			glTexSubImage3D(this.getType().getGLEnum(), 0, 0, 0, layer, width, height, 1, format.getGLEnum(), dataType.getGLEnum(), data);
			return !GLErrors.checkForError(TAG, "glTexSubImage3D");
		});
	}

	/**
	 * Sets the whole data of this array.
	 * 
	 * @param internalFormat
	 *            the internal format to use
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the depth
	 * @param format
	 *            the format
	 * @param dataType
	 *            the type of data
	 * @param data
	 *            the data to set
	 * @return whether the operation succeeded
	 */
	protected boolean setData(InternalFormat internalFormat, int width, int height, int depth, Format format, DataType dataType, ByteBuffer data) {
		return this.whileBound((Texture2DArray texture) -> {
			glTexImage3D(this.getType().getGLEnum(), 0, internalFormat.getGLEnum(), width, height, depth, 0, format.getGLEnum(), dataType.getGLEnum(), data);
			return !GLErrors.checkForError(TAG, "glTexImage3D");
		});
	}

	@Override
	public boolean setWrapMode(WrapMode mode) {
		return this.whileBound((Texture texture) -> texture.setWrapModeR(mode) && texture.setWrapModeS(mode) && texture.setWrapModeT(mode));
	}
}
