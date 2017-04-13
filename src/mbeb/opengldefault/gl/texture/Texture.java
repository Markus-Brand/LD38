package mbeb.opengldefault.gl.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.openglcontext.ContextBindings;

/**
 * Represents any texture created with OpenGL.
 * 
 * @author Potti
 * @version 1.0
 */
public abstract class Texture extends GLObject {

	private static final String TAG = "Texture";

	//<editor-fold desc="Enumerations">
	/**
	 * The type of a OpenGL texture.
	 * The constants of this enumerated type provide the value of the OpenGL enumeration to match their type.
	 */
	public enum Type {
		/**
		 * A two dimensional texture.
		 */
		TEXTURE_2D(GL_TEXTURE_2D),
		/**
		 * An array of two dimensional textures.
		 */
		TEXTURE_2D_ARRAY(GL_TEXTURE_2D_ARRAY),
		/**
		 * A three dimensional texture.
		 */
		TEXTURE_3D(GL_TEXTURE_3D),
		/**
		 * A texture that can be sampled in all directions.
		 */
		TEXTURE_CUBE_MAP(GL_TEXTURE_CUBE_MAP);

		private int glEnum;

		Type(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this texture type
		 */
		public int getGLEnum() {
			return glEnum;

		}
	}

	/**
	 * The wrap mode of a OpenGL texture.
	 * This determines how sampling out of texture range is handled.
	 * The constants of this enumerated type provide the value of the OpenGL enumeration to match their mode.
	 */
	public enum WrapMode {
		/**
		 * If the texture is sampled outside of its range, a user specified border color is returned.
		 */
		CLAMP_TO_BORDER(GL_CLAMP_TO_BORDER),
		/**
		 * If the texture is sampled outside of its range, the texture coordinate is clamped to its range.
		 */
		CLAMP_TO_EDGE(GL_CLAMP_TO_EDGE),
		/**
		 * If the texture is sampled outside of its range, the texture is sampled as if mirrored.
		 * Please note this is comparable to placing oneself between to mirrors, so if you sample further out of range,
		 * the results are the original texture once again.
		 */
		MIRRORED_REPEAT(GL_MIRRORED_REPEAT),
		/**
		 * If the texture is sampled outside of its range, the texture repeats itself.
		 * This is comparable to taking the texture coordinate modulo 1.0.
		 */
		REPEAT(GL_REPEAT);

		private int glEnum;

		WrapMode(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this wrap mode
		 */
		public int getGLEnum() {
			return glEnum;
		}
	}

	/**
	 * The minification filter of an OpenGL texture.
	 * This determines how samples of an area larger than the texel size are handled.
	 */
	public enum MinificationFilter {
		/**
		 * The value of the nearest (Manhattan-Distance) texel is returned.
		 */
		NEAREST(GL_NEAREST),
		/**
		 * The four nearest texels are weighted based on distance.
		 */
		LINEAR(GL_LINEAR),
		/**
		 * The value is calculated by using NEAREST on the mipmap level closest to the desired level of detail.
		 */
		NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST),
		/**
		 * The value is calculated by using LINEAR on the mipmap level closest to the desired level of detail.
		 */
		LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST),
		/**
		 * The values of NEAREST on the two closest mipmap levels are weighted based on distance.
		 */
		NEAREST_MIPMAP_LINEAR(GL_NEAREST_MIPMAP_LINEAR),
		/**
		 * The values of LINEAR on the two closest mipmap levels are weighted based on distance.
		 */
		LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR);

		private int glEnum;

		MinificationFilter(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this minification filter
		 */
		public int getGLEnum() {
			return glEnum;
		}
	}

	/**
	 * The magnification filter of an OpenGL texture.
	 * This determines how samples of an area smaller than the texel size are handled.
	 */
	public enum MagnificationFilter {
		/**
		 * The value of the nearest (Manhattan-Distance) texel is returned.
		 */
		NEAREST(GL_NEAREST),
		/**
		 * The four nearest texels are weighted based on distance.
		 */
		LINEAR(GL_LINEAR);

		private int glEnum;

		MagnificationFilter(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this magnification filter
		 */
		public int getGLEnum() {
			return glEnum;
		}
	}

	/**
	 * The comparison mode of an OpenGL texture with a depth component.
	 * This determines whether it can be used as a normal or shadow sampler.
	 */
	public enum CompareMode {
		/**
		 * The texture cannot be used as a shadow sampler, the depth is returned as the r component.
		 */
		NONE(GL_NONE),
		/**
		 * The texture can be used as a shadow sampler, the depth is compared to an additional given component.
		 */
		REFERENCE(GL_COMPARE_REF_TO_TEXTURE);

		private int glEnum;

		CompareMode(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this comparison mode
		 */
		public int getGLEnum() {
			return glEnum;
		}
	}

	/**
	 * The comparison function of an OpenGL texture with a depth component and {@link CompareMode#REFERENCE}.
	 * This determines when a comparison with this texture passes.
	 */
	public enum CompareFunction {
		ALWAYS(GL_ALWAYS), NEVER(GL_NEVER), GREATER(GL_GREATER), GREATER_EQUAL(GL_GEQUAL), LESS(GL_LESS), LESS_EQUAL(GL_LEQUAL), EQUAL(GL_EQUAL), NOT_EQUAL(GL_NOTEQUAL);

		private int glEnum;

		CompareFunction(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this comparison function
		 */
		public int getGLEnum() {
			return glEnum;
		}
	}

	/**
	 * The internal format of an OpenGL texture.
	 * This determines the number of components for this texture.
	 */
	public enum InternalFormat {
		RED(GL_RED, Format.RED), RG(GL_RG, Format.RED), RGB(GL_RGB, Format.RED), RGBA(GL_RGBA, Format.RED), RGBA8(GL_RGBA8, Format.RED), DEPTH(GL_DEPTH_COMPONENT, Format.DEPTH),
		STENCIL(GL_STENCIL_INDEX, Format.STENCIL);

		private int glEnum;
		/**
		 * A suitable Format to pass OpenGL to initialize the texture.
		 */
		private Format minimalData;

		InternalFormat(int glEnum, Format minimalData) {
			this.glEnum = glEnum;
			this.minimalData = minimalData;
		}

		/**
		 * @return the OpenGL enum representing this internal format
		 */
		public int getGLEnum() {
			return glEnum;
		}

		/**
		 * @return a suitable Format to pass OpenGL for this texture
		 */
		public Format getMinimalData() {
			return minimalData;
		}
	}

	/**
	 * The type of data passed to OpenGL to initialize a texture.
	 */
	public enum DataType {
		UNSIGNED_BYTE(GL_UNSIGNED_BYTE), HALF_FLOAT(GL_HALF_FLOAT), FLOAT(GL_FLOAT);

		private int glEnum;

		DataType(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this data type
		 */
		public int getGLEnum() {
			return glEnum;
		}
	}

	/**
	 * The format of the data passed to OpenGL to initialize a texture.
	 */
	public enum Format {
		RED(GL_RED), RG(GL_RG), RGB(GL_RGB), RGBA(GL_RGBA), DEPTH(GL_DEPTH_COMPONENT), STENCIL(GL_STENCIL_INDEX);

		private int glEnum;

		Format(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum representing this format
		 */
		public int getGLEnum() {
			return glEnum;
		}
	}
	//</editor-fold>

	/**
	 * The type of this texture.
	 */
	private Type type;

	//<editor-fold desc="Texture IO">
	/**
	 * Generate ByteBuffer from BufferedImage
	 *
	 * @param image
	 *            input BufferedImage
	 * @param flipped
	 *            flip on y coordinate
	 * @return generated ByteBuffer
	 */
	public static ByteBuffer generateBuffer(final BufferedImage image, final boolean flipped) {
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		// create the openGL Buffer object
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

		// copy data to the buffer
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int sampleY = flipped ? (image.getHeight() - y - 1) : y;
				int pixel = pixels[sampleY * image.getWidth() + x];
				buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
				buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha component.
			}
		}
		buffer.flip();
		return buffer;
	}

	/**
	 * Loads a BufferedImage from specified path within the textures folder
	 *
	 * @param path
	 *            Path of the Image
	 * @return loaded BufferedImage
	 */
	public static BufferedImage loadBufferedImage(final String path) {
		InputStream in = ClassLoader.getSystemResourceAsStream("textures/" + path);
		BufferedImage image = null;
		try {
			image = ImageIO.read(in);
		} catch(IOException e) {
			Log.error(TAG, "Unable to Load Texture: " + path, e);
		}
		return image;
	}
	//</editor-fold>

	//<editor-fold desc="Texture unit control">
	/**
	 * Makes the given texture unit the active texture unit.
	 * 
	 * @param unit
	 *            the texture unit
	 * @return whether the operation succeeded
	 */
	protected static boolean setActiveTexture(final Integer unit) {
		if (ContextBindings.setActiveTextureUnit(unit)) {
			glActiveTexture(unit != null ? GL_TEXTURE0 + unit : 0);
			boolean success = !GLErrors.checkForError(TAG, "Could not set active texture.");
			if (!success) {
				ContextBindings.setActiveTextureUnit(null);
			}
			return success;
		} else {
			return true;
		}
	}

	/**
	 * Makes the texture unit this texture is bound to the active texture unit.
	 * 
	 * @return whether the operation succeeded
	 */
	protected final boolean setActiveTexture() {
		if (this.getHandle() != null && this.getTextureUnit() != null) {
			return Texture.setActiveTexture(this.getTextureUnit());
		} else {
			return false;
		}
	}
	//</editor-fold>

	/**
	 * Creates a new texture.
	 * The created texture is not generated yet.
	 * 
	 * @param type
	 *            the type of the texture to create
	 */
	protected Texture(final Type type) {
		this.type = type;
	}

	//<editor-fold desc="Accessors">
	/**
	 * @return the texture unit this texture is bound to, null if unbound
	 */
	public final Integer getTextureUnit() {
		return ContextBindings.getTextureUnit(this);
	}

	/**
	 * @return the type of this texture
	 */
	public final Type getType() {
		return type;
	}
	//</editor-fold>

	//<editor-fold desc="GLObject implementation">
	@Override
	protected final Integer glGenerate() {
		int handle = glGenTextures();
		boolean success = !GLErrors.checkForError(TAG, "Could not generate a " + this.getType().name() + ".");
		if (success) {
			return handle;
		} else {
			return null;
		}
	}

	@Override
	protected final boolean glBind() {
		ContextBindings.bind(this);
		if (this.setActiveTexture()) {
			glBindTexture(this.getType().getGLEnum(), this.getHandle());
			boolean success = !GLErrors.checkForError(TAG, "Could not bind a " + this.getType().name() + ".");
			if (!success) {
				ContextBindings.unbind(this);
			}
			return success;
		} else {
			ContextBindings.unbind(this);
			return false;
		}
	}

	@Override
	protected final boolean isBoundToContext() {
		return ContextBindings.isBound(this);
	}

	@Override
	protected final boolean glUnbind() {
		return ContextBindings.unbind(this);
	}

	@Override
	protected final boolean glBeginTransaction() {
		return this.setActiveTexture();
	}

	@Override
	protected final boolean glDelete() {
		glDeleteTextures(this.getHandle());
		return !GLErrors.checkForError(TAG, "Could not delete a " + this.getType().name() + ".");
	}
	//</editor-fold>

	//<editor-fold desc="Parameter access">
	/**
	 * Invokes glTexParameteri with the given parameter and value, while ensuring this texture is the active texture.
	 * 
	 * @param parameter
	 *            the parameter to set
	 * @param value
	 *            the value to set
	 * @return whether the operation succeeded
	 */
	protected final boolean setParameter(final int parameter, final int value) {
		return this.whileBound(texture -> {
			glTexParameteri(this.getType().getGLEnum(), parameter, value);
			return !GLErrors.checkForError(TAG, "glTexParameteri");
		});
	}

	/**
	 * Sets the wrap mode of the r texture coordinate.
	 * 
	 * @param mode
	 *            the mode to set
	 * @return whether the operation succeeded
	 */
	public final boolean setWrapModeR(final WrapMode mode) {
		boolean success = this.setParameter(GL_TEXTURE_WRAP_R, mode.getGLEnum());
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_WRAP_R.");
		}
		return success;
	}

	/**
	 * Sets the wrap mode of the s texture coordinate.
	 * 
	 * @param mode
	 *            the mode to set
	 * @return whether the operation succeeded
	 */
	public final boolean setWrapModeS(final WrapMode mode) {
		boolean success = this.setParameter(GL_TEXTURE_WRAP_S, mode.getGLEnum());
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_WRAP_S.");
		}
		return success;
	}

	/**
	 * Sets the wrap mode of the t texture coordinate.
	 * 
	 * @param mode
	 *            the mode to set
	 * @return whether the operation succeeded
	 */
	public final boolean setWrapModeT(final WrapMode mode) {
		boolean success = this.setParameter(GL_TEXTURE_WRAP_T, mode.getGLEnum());
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_WRAP_T.");
		}
		return success;
	}

	/**
	 * Sets the minification filter of this texture.
	 * 
	 * @param filter
	 *            the filter to set
	 * @return whether the operation succeeded
	 */
	public final boolean setMinificationFilter(final MinificationFilter filter) {
		boolean success = this.setParameter(GL_TEXTURE_MIN_FILTER, filter.getGLEnum());
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_MIN_FILTER.");
		}
		return success;
	}

	/**
	 * Sets the magnification filter of this texture.
	 * 
	 * @param filter
	 *            the filter to set
	 * @return whether the operation succeeded
	 */
	public final boolean setMagnificationFilter(final MagnificationFilter filter) {
		boolean success = this.setParameter(GL_TEXTURE_MAG_FILTER, filter.getGLEnum());
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_MAG_FILTER.");
		}
		return success;
	}

	/**
	 * Sets the comparison mode of this texture.
	 *
	 * @param mode
	 *            the mode to set
	 * @return whether the operation succeeded
	 */
	public final boolean setCompareMode(final CompareMode mode) {
		boolean success = this.setParameter(GL_TEXTURE_COMPARE_MODE, mode.getGLEnum());
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_COMPARE_MODE.");
		}
		return success;
	}

	/**
	 * Sets the comparison function of this texture.
	 *
	 * @param function
	 *            the function to set
	 * @return whether the operation succeeded
	 */
	public final boolean setCompareFunction(final CompareFunction function) {
		boolean success = this.setParameter(GL_TEXTURE_COMPARE_FUNC, function.getGLEnum());
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_COMPARE_FUNC.");
		}
		return success;
	}

	/**
	 * Sets the border color of this texture.
	 * 
	 * @param color
	 *            the color to set
	 * @return whether the operation succeeded
	 */
	public final boolean setBorderColor(final Color color) {
		return this.whileBound(texture -> {
			final float maxValue = 255.0f;
			final float[] colorData = new float[] {color.getRed() / maxValue, color.getGreen() / maxValue, color.getBlue() / maxValue, color.getAlpha() / maxValue};
			glTexParameterfv(this.getType().getGLEnum(), GL_TEXTURE_BORDER_COLOR, colorData);
			return !GLErrors.checkForError(TAG, "glTexParameterfv");
		});
	}

	/**
	 * @param level
	 *            the minimum level this texture can be sampled from
	 * @return whether the operation succeeded
	 */
	public final boolean setBaseLevel(final int level) {
		boolean success = this.setParameter(GL_TEXTURE_BASE_LEVEL, level);
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_BASE_LEVEL.");
		}
		return success;
	}

	/**
	 * @param level
	 *            the maximum level this texture can be sampled from
	 * @return whether the operation succeeded
	 */
	public final boolean setMaxLevel(final int level) {
		boolean success = this.setParameter(GL_TEXTURE_MAX_LEVEL, level);
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_MAX_LEVEL.");
		}
		return success;
	}

	/**
	 * Sets whether this texture interpolates on texture fetches.
	 * Setting this to true results in a texture with {@link MagnificationFilter#LINEAR} and
	 * {@link MinificationFilter#LINEAR_MIPMAP_LINEAR}.
	 * Setting this to false results in a texture with {@link MagnificationFilter#NEAREST} and
	 * {@link MinificationFilter#NEAREST}.
	 *
	 * @param interpolate
	 *            whether to interpolate
	 * @return whether the operation succeeded
	 */
	public final boolean setInterpolates(final boolean interpolate) {
		return this.whileBound(texture -> this.setMagnificationFilter(interpolate ? MagnificationFilter.LINEAR : MagnificationFilter.NEAREST)
				&& this.setMinificationFilter(interpolate ? MinificationFilter.LINEAR_MIPMAP_LINEAR : MinificationFilter.NEAREST));
	}

	/**
	 * Sets all appropriate wrap modes to the given mode.
	 * 
	 * @param mode
	 *            the mode to set
	 * @return whether the operation succeeded
	 */
	public abstract boolean setWrapMode(final WrapMode mode);
	//</editor-fold>

	/**
	 * Generates mipmap levels for this texture.
	 * 
	 * @return whether the operation succeeded
	 */
	public final boolean generateMipmaps() {
		return this.whileBound(texture -> {
			glGenerateMipmap(this.getType().getGLEnum());
			return !GLErrors.checkForError(TAG, "glGenerateMipmap");
		});
	}
}
