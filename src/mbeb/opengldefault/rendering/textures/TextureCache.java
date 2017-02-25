package mbeb.opengldefault.rendering.textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

import javax.imageio.*;

import mbeb.opengldefault.logging.*;

import org.lwjgl.*;

/**
 * static methods for creating / using textures.
 * Used by {@link Texture} and {@link CubeMap}
 */
public class TextureCache {

	/** Class Name Tag */
	private static final String TAG = "TextureCache";

	private static Map<String, Integer> cachedImages = new HashMap<>();

	/**
	 * load Texture with given path
	 *
	 * @param path
	 * @return
	 */
	public static int loadTexture(String path) {
		return loadTexture(path, true);
	}

	/**
	 * load Texture with given path and given interpolation method
	 *
	 * @param path
	 * @param interpolate
	 * @return openGl texture
	 */
	public static int loadTexture(String path, boolean interpolate) {
		return loadTexture(path, interpolate, GL_REPEAT, GL_REPEAT);
	}

	/**
	 * load Texture with given path and given interpolation and wrappinh method.
	 * Loads the texture from the cache, in case that we already loaded this image
	 *
	 * @param path
	 * @param interpolate
	 * @param wrapS
	 * @param wrapT
	 * @return openGl texture
	 */
	public static int loadTexture(String path, boolean interpolate, int wrapS, int wrapT) {
		String key = path + interpolate + wrapS + wrapT;
		Integer texture = cachedImages.get(key);
		if (texture == null) {
			Log.log(TAG, "loaded Image: " + path);
			BufferedImage img = loadBufferedImage(path);
			texture = loadTexture(img, interpolate, wrapS, wrapT);
			cachedImages.put(key, texture);
			return texture;
		}
		return texture.intValue();
	}

	/**
	 * Loads a BufferedImage from specified path within the textures folder
	 *
	 * @param path
	 *            Path of the Image
	 * @return loaded BufferedImage
	 */
	private static BufferedImage loadBufferedImage(String path) {
		InputStream in = ClassLoader.getSystemResourceAsStream("textures/" + path);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch(IOException e) {
			Log.error(TAG, "Unable to Load Texture: " + path);
			e.printStackTrace();
		}
		return img;
	}

	/**
	 * Generate OpenGL Texture from BufferedImage with given interpolation method
	 *
	 * @param image
	 *            input BufferedImage
	 * @param interpolate
	 *            interpolation method
	 * @return openGl texture
	 */
	public static int loadTexture(BufferedImage image, boolean interpolate, int wrapS, int wrapT) {

		ByteBuffer buffer = generateBuffer(image);

		int texture = glGenTextures();
		GLErrors.checkForError(TAG, "glGenTextures");

		glActiveTexture(GL_TEXTURE0 + texture);
		GLErrors.checkForError(TAG, "glActiveTexture");

		glBindTexture(GL_TEXTURE_2D, texture);
		GLErrors.checkForError(TAG, "glBindTexture");

		setTexParameter(interpolate, wrapS, wrapT);
		GLErrors.checkForError(TAG, "setTexParameter");

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		GLErrors.checkForError(TAG, "glTexImage2D");

		if (interpolate) {
			glGenerateMipmap(GL_TEXTURE_2D);
			GLErrors.checkForError(TAG, "glGenerateMipmap");
		}

		glBindTexture(GL_TEXTURE_2D, 0);
		GLErrors.checkForError(TAG, "glBindTexture");
		return texture;
	}

	/**
	 * Load Cube Map with given path
	 *
	 * @param path
	 *            Path in the texture folder
	 * @param interpolate
	 *            toggles interpolation
	 * @param wrapS
	 *            sets WrapMode in x direction
	 * @param wrapT
	 *            sets WrapMode in y direction
	 * @return OpenGL texture handle
	 */
	public static int loadCubeMap(String path) {
		String key = path;
		Integer texture = cachedImages.get(key);
		if (texture == null) {
			Log.log(TAG, "loaded Cube Map: " + path);
			String imageFormat = ".jpg";
			BufferedImage[] img = new BufferedImage[6];
			img[0] = loadBufferedImage(path + "_r" + imageFormat);
			img[1] = loadBufferedImage(path + "_l" + imageFormat);
			img[2] = loadBufferedImage(path + "_top" + imageFormat);
			img[3] = loadBufferedImage(path + "_bot" + imageFormat);
			img[4] = loadBufferedImage(path + "_b" + imageFormat);
			img[5] = loadBufferedImage(path + "_f" + imageFormat);
			texture = loadCubeMap(img);
			cachedImages.put(key, texture);
			return texture;
		}
		return texture.intValue();
	}

	/**
	 * Generate OpenGL CubeMap from BufferedImage Array with given interpolation method
	 *
	 * @param image
	 *            input BufferedImage Array
	 * @param interpolate
	 *            interpolation method
	 * @param wrapS
	 *            sets WrapMode in x direction
	 * @param wrapT
	 *            sets WrapMode in y direction
	 * @return openGl texture
	 */
	public static int loadCubeMap(BufferedImage[] images) {

		int texture = glGenTextures();
		GLErrors.checkForError(TAG, "glGenTextures");

		glActiveTexture(GL_TEXTURE0 + texture);
		GLErrors.checkForError(TAG, "glActiveTexture");

		glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
		GLErrors.checkForError(TAG, "glBindTexture");

		for (int i = 0; i < images.length; i++) {
			ByteBuffer buffer = generateBuffer(images[i]);

			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA8, images[i].getWidth(), images[i].getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			GLErrors.checkForError(TAG, "glTexImage2D");
		}

		setTexParameterCubemap();

		glBindTexture(GL_TEXTURE_2D, 0);
		return texture;
	}

	/**
	 * set Texture Parameter
	 *
	 * @param interpolate
	 *            interpolation method
	 * @param wrapS
	 *            sets WrapMode in x direction
	 * @param wrapT
	 *            sets WrapMode in y direction
	 */
	private static void setTexParameter(boolean interpolate, int wrapS, int wrapT) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
		if (interpolate) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		} else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		}
		GLErrors.checkForError(TAG, "glTexParameteri");
	}

	/**
	 * set Texture Parameter
	 *
	 * @param interpolate
	 *            interpolation method
	 * @param wrapS
	 *            sets WrapMode in x direction
	 * @param wrapT
	 *            sets WrapMode in y direction
	 */
	private static void setTexParameterCubemap() {
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_BASE_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, 0);

		GLErrors.checkForError(TAG, "glTexParameteri");
	}

	/**
	 * Generate ByteBuffer from BufferedImage
	 *
	 * @param image
	 *            input BufferedImage
	 * @return generated ByteBuffer
	 */
	public static ByteBuffer generateBuffer(BufferedImage image) {
		// fetch all color data from image to array
		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		// create the openGL Buffer object
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * (image.getType() == BufferedImage.TYPE_INT_ARGB ? 4 : 4));

		// copy data to the buffer
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
				buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha component.
				// Only for RGBA
			}
		}
		buffer.flip();
		return buffer;
	}

	/**
	 * Deletes all OpenGL textures and clears the cache
	 */
	public static void clearCache() {
		for (int value : cachedImages.values()) {
			glDeleteTextures(value);
			GLErrors.checkForError(TAG, "glDeleteTextures");
		}
		cachedImages = new HashMap<>();
	}
}
