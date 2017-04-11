package mbeb.opengldefault.gl.texture;


import mbeb.opengldefault.logging.GLErrors;

import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL13.*;

/**
 * Represents a cube map texture
 */
public class CubeMap extends Texture {
	private static final String TAG = "CubeMap";

	/**
	 * The face of a cube map texture.
	 */
	public enum Face {
		/**
		 * The right face.
		 */
		POSITIVE_X(GL_TEXTURE_CUBE_MAP_POSITIVE_X),
		/**
		 * The left face.
		 */
		NEGATIVE_X(GL_TEXTURE_CUBE_MAP_NEGATIVE_X),
		/**
		 * The top face.
		 */
		POSITIVE_Y(GL_TEXTURE_CUBE_MAP_POSITIVE_Y),
		/**
		 * The bottom face.
		 */
		NEGATIVE_Y(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y),
		/**
		 * The back face.
		 */
		POSITIVE_Z(GL_TEXTURE_CUBE_MAP_POSITIVE_Z),
		/**
		 * The front face.
		 */
		NEGATIVE_Z(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);

		private int glEnum;

		Face(int glEnum) {
			this.glEnum = glEnum;
		}

		public int getGlEnum() {
			return glEnum;
		}
	}

	/**
	 * Creates a new cube map.
	 * The created texture is not generated yet.
	 *
	 */
	public CubeMap() {
		super(Type.TEXTURE_CUBE_MAP);
	}

	public CubeMap (BufferedImage[] images) {
		this();
		this.ensureExists();
		this.beginTransaction();
		for (int i = 0; i < images.length; i++) {
			ByteBuffer buffer = Texture.generateBuffer(images[i], false);

			glTexImage2D(Face.POSITIVE_X.getGlEnum() + i, 0, InternalFormat.RGBA8.getGLEnum(), images[i].getWidth(), images[i].getHeight(),
					0, Format.RGBA.getGLEnum(), DataType.UNSIGNED_BYTE.getGLEnum(), buffer);
			GLErrors.checkForError(TAG, "glTexImage2D");
		}
		this.finishTransaction();
	}

	public CubeMap(String path) {
		this(TextureLoader.loadCubeMapImages(path));
		this.beginTransaction();
		this.setWrapModeR(WrapMode.CLAMP_TO_EDGE);
		this.setWrapModeS(WrapMode.CLAMP_TO_EDGE);
		this.setWrapModeT(WrapMode.CLAMP_TO_EDGE);
		this.setMinificationFilter(MinificationFilter.LINEAR);
		this.setMagnificationFilter(MagnificationFilter.LINEAR);
		this.setMaxLevel(0);
		this.setBaseLevel(0);
		this.finishTransaction();
	}


}
