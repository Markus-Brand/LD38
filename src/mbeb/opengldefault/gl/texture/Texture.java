package mbeb.opengldefault.gl.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.logging.GLErrors;
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

	private boolean temporaryBinding = false;
	private Type type;

	protected static boolean setActiveTexture(Integer unit) {
		glActiveTexture(unit != null ? GL_TEXTURE0 + unit : 0);
		return !GLErrors.checkForError(TAG, "Could not set active texture.");
	}

	protected boolean setActiveTexture() {
		if (this.getHandle() != null && this.getTextureUnit() != null) {
			return Texture.setActiveTexture(this.getTextureUnit());
		} else {
			return false;
		}
	}

	protected Texture(Type type) {
		this.type = type;
	}

	public Integer getTextureUnit() {
		return ContextBindings.getTextureUnit(this);
	}

	public Type getType() {
		return type;
	}

	/**
	 * Checks whether this texture is bound or not.
	 * If it is bound, it changes the active texture unit to the unit it is bound to.
	 * If in is not bound, it starts a temporary binding and binds the object.
	 * This makes sure any glTex* calls affect this texture.
	 *
	 * @return whether the operation succeeded
	 */
	protected final boolean beginTransaction() {
		if (this.isBound()) {
			return this.setActiveTexture();
		} else {
			boolean success = this.bind();
			this.temporaryBinding = success;
			return success;
		}
	}

	protected final boolean finishTransaction() {
		if (this.temporaryBinding) {
			boolean success = this.unbind();
			this.temporaryBinding = !success;
			return success;
		} else {
			return true;
		}
	}

}
