package mbeb.opengldefault.gl.texture;

import mbeb.opengldefault.logging.GLErrors;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;

/**
 * Represents any texture that can hold three dimensional data.
 *
 * Currently this is both a Texture2DArray or a Texture3D.
 * This class' sole purpose is the elimination of code duplication.
 */
public abstract class ThreeDimensionalTexture extends Texture {
	private static final String TAG = "ThreeDimensionalTexture";

	public ThreeDimensionalTexture(Type type) {
		super(type);
	}

	/**
	 * Sets a single image of this three dimensional texture.
	 *
	 * @param z
	 *            the z coordinate of the layer to set
	 * @param image
	 *            the image to set
	 * @return whether the operation succeeded
	 */
	public boolean setLayer(int z, BufferedImage image) {
		return this.whileBound((Texture3D texture) -> {
			ByteBuffer data = Texture.generateBuffer(image, true);
			return this.setLayerData(image.getWidth(), image.getHeight(), z, Format.RGBA, DataType.UNSIGNED_BYTE, data);
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
		return this.whileBound((Texture3D texture) -> {
			boolean success = true;
			for (int i = 0; i < images.length && success; i++) {
				success = this.setLayer(i, images[i]);
			}
			return success;
		});
	}

	/**
	 * Sets data for one image of this three dimensional texture.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param depth
	 *            the z coordinate of the layer to set
	 * @param format
	 *            the format of the data
	 * @param dataType
	 *            the type of the data
	 * @param data
	 *            the data
	 * @return whether the operation succeeded
	 */
	protected boolean setLayerData(int width, int height, int depth, Format format, DataType dataType, ByteBuffer data) {
		return this.whileBound((Texture3D texture) -> {
			glTexSubImage3D(this.getType().getGLEnum(), 0, 0, 0, depth, width, height, 1, format.getGLEnum(), dataType.getGLEnum(), data);
			return !GLErrors.checkForError(TAG, "glTexSubImage3D");
		});
	}

	/**
	 * Sets the whole data of this texture.
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
		return this.whileBound((Texture3D texture) -> {
			glTexImage3D(this.getType().getGLEnum(), 0, internalFormat.getGLEnum(), width, height, depth, 0, format.getGLEnum(), dataType.getGLEnum(), data);
			return !GLErrors.checkForError(TAG, "glTexImage3D");
		});
	}

	@Override
	public boolean setWrapMode(WrapMode mode) {
		return this.whileBound((Texture texture) -> texture.setWrapModeR(mode) && texture.setWrapModeS(mode) && texture.setWrapModeT(mode));
	}
}
