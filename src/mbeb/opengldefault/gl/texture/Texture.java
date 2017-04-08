package mbeb.opengldefault.gl.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

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
	//</editor-fold>

	/**
	 * The current transaction level.
	 * This stores how many transactions are currently in progress.
	 */
	private int transactionLevel = 0;
	/**
	 * Whether the texture is currently bound temporarily, for the purposes of a transaction.
	 */
	private boolean temporaryBinding = false;
	/**
	 * The type of this texture.
	 */
	private Type type;

	//<editor-fold desc="Texture unit control">
	/**
	 * Makes the given texture unit the active texture unit.
	 * 
	 * @param unit
	 *            the texture unit
	 * @return whether the operation succeeded
	 */
	protected static boolean setActiveTexture(Integer unit) {
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
	protected boolean setActiveTexture() {
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
	protected Texture(Type type) {
		this.type = type;
	}

	//<editor-fold desc="Accessors">
	/**
	 * @return the texture unit this texture is bound to, null if unbound
	 */
	public Integer getTextureUnit() {
		return ContextBindings.getTextureUnit(this);
	}

	/**
	 * @return the type of this texture
	 */
	public Type getType() {
		return type;
	}
	//</editor-fold>

	//<editor-fold desc="GLObject implementation">
	@Override
	protected Integer glGenerate() {
		int handle = glGenTextures();
		boolean success = !GLErrors.checkForError(TAG, "Could not generate a " + this.getType().name() + ".");
		if (success) {
			return handle;
		} else {
			return null;
		}
	}

	@Override
	protected boolean glBind() {
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
	protected boolean isBoundToContext() {
		return ContextBindings.isBound(this);
	}

	@Override
	protected boolean glUnbind() {
		return ContextBindings.unbind(this);
	}

	@Override
	protected boolean glDelete() {
		glDeleteTextures(this.getHandle());
		return !GLErrors.checkForError(TAG, "Could not delete a " + this.getType().name() + ".");
	}
	//</editor-fold>

	//<editor-fold desc="Transactions">
	/**
	 * Ensures this texture is bound after this method has been called.
	 * If it is bound, it changes the active texture unit to the unit it is bound to.
	 * If in is not bound, it starts a temporary binding and binds the object.
	 * This makes sure any glTex* calls affect this texture.
	 *
	 * @return whether the operation succeeded
	 */
	protected final boolean beginTransaction() {
		if (this.isBound()) {
			boolean success = this.setActiveTexture();
			if (success) {
				this.transactionLevel++;
			}
			return success;
		} else {
			boolean success = this.bind();
			this.temporaryBinding = success;
			if (success) {
				this.transactionLevel++;
			}
			return success;
		}
	}

	/**
	 * Releases a temporary binding created by {@link #beginTransaction()}.
	 * 
	 * @return whether the operation succeeded
	 */
	protected final boolean finishTransaction() {
		if (this.transactionLevel > 0) {
			this.transactionLevel--;
			if (this.temporaryBinding && this.transactionLevel == 0) {
				boolean success = this.unbind();
				this.temporaryBinding = !success;
				return success;
			} else {
				return true;
			}
		} else {
			return true;
		}
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
	protected boolean setParameter(final int parameter, final int value) {
		if (this.beginTransaction()) {
			glTexParameteri(this.getType().getGLEnum(), parameter, value);
			boolean success = !GLErrors.checkForError(TAG, "glTexParameteri");
			if (!this.finishTransaction()) {
				Log.error(TAG, "Failed to finish transaction.");
			}
			return success;
		} else {
			return false;
		}
	}

	/**
	 * Sets the wrap mode of the r texture coordinate.
	 * 
	 * @param mode
	 *            the mode to set
	 * @return whether the operation succeeded
	 */
	public boolean setWrapModeR(WrapMode mode) {
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
	public boolean setWrapModeS(WrapMode mode) {
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
	public boolean setWrapModeT(WrapMode mode) {
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
	public boolean setMinificationFilter(MinificationFilter filter) {
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
	public boolean setMagnificationFilter(MagnificationFilter filter) {
		boolean success = this.setParameter(GL_TEXTURE_MAG_FILTER, filter.getGLEnum());
		if (!success) {
			Log.error(TAG, "Failed to set TEXTURE_MAG_FILTER.");
		}
		return success;
	}
	//</editor-fold>

}
