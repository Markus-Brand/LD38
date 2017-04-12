package mbeb.opengldefault.gl.texture;

import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL13.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import mbeb.opengldefault.logging.GLErrors;

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

	public static BufferedImage[] loadCubeMapImages(String path, String extension) {
		BufferedImage[] img = new BufferedImage[6];
		img[0] = Texture.loadBufferedImage(path + "_r." + extension);
		img[1] = Texture.loadBufferedImage(path + "_l." + extension);
		img[2] = Texture.loadBufferedImage(path + "_top." + extension);
		img[3] = Texture.loadBufferedImage(path + "_bot." + extension);
		img[4] = Texture.loadBufferedImage(path + "_b." + extension);
		img[5] = Texture.loadBufferedImage(path + "_f." + extension);
		return img;
	}

	public static BufferedImage[] loadCubeMapImages(String path) {
		return loadCubeMapImages(path, "jpg");
	}

	/**
	 * Creates a new cube map.
	 * The created texture is not generated yet.
	 */
	public CubeMap() {
		super(Type.TEXTURE_CUBE_MAP);
	}

	/**
	 * Creates a new cube map with the given images.
	 * 
	 * @param images
	 *            the cubemap images
	 */
	public CubeMap(BufferedImage[] images) {
		this();
		this.ensureExists();
		this.setImages(images);
	}

	/**
	 * Creates a new cube map with the images at the given path.
	 * 
	 * @param path
	 *            the path to load the images from
	 */
	public CubeMap(String path) {
		this(CubeMap.loadCubeMapImages(path));
	}

	/**
	 * Sets the given face data.
	 * 
	 * @param face_target
	 *            the GL enum for the face to set
	 * @param internalFormat
	 *            the internal format of this texture
	 * @param format
	 *            the format of the given data
	 * @param dataType
	 *            the type of the given data
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 * @param data
	 *            the data to set
	 * @return whether the operation succeeded
	 */
	protected boolean setFaceData(int face_target, InternalFormat internalFormat, Format format, DataType dataType, int width, int height, ByteBuffer data) {
		return this.whileBound(texture -> {
			glTexImage2D(face_target, 0, internalFormat.getGLEnum(), width, height, 0, format.getGLEnum(), dataType.getGLEnum(), data);
			return !GLErrors.checkForError(TAG, "glTexImage2D");
		});
	}

	/**
	 * Sets all faces of this cube map to the given images.
	 * 
	 * @param images
	 *            the images to set
	 * @return whether the operation succeeded
	 */
	public boolean setImages(BufferedImage[] images) {
		return this.whileBound(texture -> {
			boolean success = true;
			for (int i = 0; i < images.length && success; i++) {
				ByteBuffer data = Texture.generateBuffer(images[i], false);
				success = this.setFaceData(Face.POSITIVE_X.getGlEnum() + i, InternalFormat.RGBA8, Format.RGBA, DataType.UNSIGNED_BYTE, images[i].getWidth(), images[i].getHeight(), data);
			}
			return success;
		});
	}

	/**
	 * Sets the given face to the given image.
	 * 
	 * @param face
	 *            the face to set
	 * @param image
	 *            the image to set
	 * @return whether the operation succeeded
	 */
	public boolean setFaceImage(Face face, BufferedImage image) {
		ByteBuffer data = Texture.generateBuffer(image, false);
		return setFaceData(face.getGlEnum(), InternalFormat.RGBA8, Format.RGBA, DataType.UNSIGNED_BYTE, image.getWidth(), image.getHeight(), data);
	}

	/**
	 * @param mode
	 *            the mode to set for this texture
	 * @return whether the operation succeeded
	 */
	public boolean setWrapMode(WrapMode mode) {
		return this.whileBound(texture -> this.setWrapModeR(mode) && this.setWrapModeS(mode) && this.setWrapModeT(mode));
	}

}
