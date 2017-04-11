package mbeb.opengldefault.gl.texture;

import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import mbeb.opengldefault.logging.GLErrors;
import org.lwjgl.BufferUtils;

/**
 * Represents a two-dimensional texture.
 * 
 * @author Potti
 * @version 1.0
 */
public class Texture2D extends Texture {

	private static final String TAG = "Texture2D";

	/**
	 * Creates a new two dimensional texture.
	 * This texture will not be initialized yet.
	 */
	protected Texture2D() {
		super(Type.TEXTURE_2D);
	}

	/**
	 * Creates a new two dimensional texture with the given width, height and data.
	 * 
	 * @param width
	 *            the height of the texture
	 * @param height
	 *            the width of the texture
	 * @param format
	 *            the format of the texture
	 */
	public Texture2D(int width, int height, InternalFormat format) {
		this();
		this.ensureExists();
		this.setImageData(width, height, format, Format.RED, DataType.UNSIGNED_BYTE, null);
		this.beginTransaction();
		this.setMagnificationFilter(MagnificationFilter.NEAREST);
		this.setMinificationFilter(MinificationFilter.NEAREST);
		this.setWrapModeS(WrapMode.CLAMP_TO_EDGE);
		this.setWrapModeT(WrapMode.CLAMP_TO_EDGE);
		this.finishTransaction();
	}

	/**
	 * Creates a 2D texture and initializes it with the given image.
	 * 
	 * @param image
	 *            the image to set
	 */
	public Texture2D(BufferedImage image) {
		this();
		this.ensureExists();
		ByteBuffer imageData = Texture.generateBuffer(image, true);
		this.setImageData(image.getWidth(), image.getHeight(), InternalFormat.RGBA8, Format.RGBA, DataType.UNSIGNED_BYTE, imageData);
	}

	/**
	 * Creates a 2D texture and initializes it with the given image.
	 *
	 * @param image
	 *            the image to set
	 */
	public Texture2D(BufferedImage image, boolean interpolate) {
		this(image);
		if(interpolate) {
			this.setMagnificationFilter(MagnificationFilter.LINEAR);
			this.setMinificationFilter(MinificationFilter.LINEAR_MIPMAP_LINEAR);
			this.generateMipmaps();
		} else {
			this.setMagnificationFilter(MagnificationFilter.NEAREST);
			this.setMinificationFilter(MinificationFilter.NEAREST);
		}
	}

	public Texture2D(String path){
		this(path, true);
	}

	public Texture2D(String path, boolean interpolate){
		this(path, interpolate, WrapMode.REPEAT);
	}

	public Texture2D(String path, boolean interpolate, WrapMode wrapMode){
		this(path, interpolate, wrapMode, wrapMode);
	}

	public Texture2D(String path, boolean interpolate, WrapMode modeS, WrapMode modeT) {
		this(TextureLoader.loadBufferedImage(path));
		this.beginTransaction();
		if(interpolate) {
			this.setMagnificationFilter(MagnificationFilter.LINEAR);
			this.setMinificationFilter(MinificationFilter.LINEAR_MIPMAP_LINEAR);
			this.generateMipmaps();
		} else {
			this.setMagnificationFilter(MagnificationFilter.NEAREST);
			this.setMinificationFilter(MinificationFilter.NEAREST);
		}
		this.setWrapModeS(modeS);
		this.setWrapModeT(modeT);
		this.finishTransaction();
	}

	/**
	 * Initializes the texture with the given data and format. Data may be null to prevent any pixel transfer.
	 * 
	 * @param width
	 *            the width of the texture
	 * @param height
	 *            the height of the texture
	 * @param internalFormat
	 *            the internal OpenGL format to use
	 * @param dataFormat
	 *            the format of the data
	 * @param dataType
	 *            the type of the data
	 * @param data
	 *            the data itself
	 * @return whether the operation succeeded
	 */
	protected boolean setImageData(int width, int height, InternalFormat internalFormat, Format dataFormat, DataType dataType, ByteBuffer data) {
		if (this.beginTransaction()) {
			glTexImage2D(this.getType().getGLEnum(), 0, internalFormat.getGLEnum(), width, height, 0, dataFormat.getGLEnum(), dataType.getGLEnum(), data);
			boolean success = !GLErrors.checkForError(TAG, "glTexImage2D");
			this.finishTransaction();
			return success;
		} else {
			return false;
		}
	}

	/**
	 * Hailing from ye olde times of texture access, still used in GUI.
	 * @param x
	 * @param y
	 * @param color
	 * @return whether the operation succeeded
	 * @deprecated Why only set a single pixel?
	 */
	@Deprecated
	public boolean setPixel(int x, int y, Color color) {
		if(this.beginTransaction()) {
			ByteBuffer buffer = BufferUtils.createByteBuffer(4);
			buffer.put((byte) color.getRed());
			buffer.put((byte) color.getGreen());
			buffer.put((byte) color.getBlue());
			buffer.put((byte) color.getAlpha());
			buffer.flip();
			glTexSubImage2D(this.getType().getGLEnum(), 0, x, y, 1, 1, Format.RGBA.getGLEnum(), DataType.UNSIGNED_BYTE.getGLEnum(), buffer);
			boolean success = !GLErrors.checkForError(TAG, "glTexSubImage2D");
			this.finishTransaction();
			return success;
		} else {
			return false;
		}
	}

}
