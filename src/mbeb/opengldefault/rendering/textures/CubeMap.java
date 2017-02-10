package mbeb.opengldefault.rendering.textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

public class CubeMap {

	/** Class Name Tag */
	private static final String TAG = "CubeMap";

	//Wrap methods
	private static int wrapS;
	private static int wrapT;

	static {
		wrapS = GL_REPEAT;
		wrapT = GL_REPEAT;
	}

	public static int loadCubeMap(String name) {
		return loadCubeMap(name, false);
	}

	public static int loadCubeMap(String name, boolean interpolate) {
		try {
			String imageFormat = "jpg";
			BufferedImage[] img = new BufferedImage[6];
			img[0] = loadImage(name + "_r", imageFormat);
			img[1] = loadImage(name + "_l", imageFormat);
			img[2] = loadImage(name + "_top", imageFormat);
			img[3] = loadImage(name + "_bot", imageFormat);
			img[4] = loadImage(name + "_b", imageFormat);
			img[5] = loadImage(name + "_f", imageFormat);
			return loadCubeMap(img, interpolate);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private static BufferedImage loadImage(String name, String format) throws IOException, URISyntaxException {
		return ImageIO.read(CubeMap.class.getResource("../tex/" + name + "." + format).toURI().toURL());
	}

	/**
	 * Generate OpenGL CubeMap from BufferedImage Array with given interpolation method
	 *
	 * @param image
	 *            input BufferedImage Array
	 * @param interpolate
	 *            interpolation method
	 * @return openGl texture
	 */
	public static int loadCubeMap(BufferedImage[] images, boolean interpolate) {

		int texture = glGenTextures();
		glActiveTexture(GL_TEXTURE_CUBE_MAP + texture);

		for (int i = 0; i < images.length; i++) {
			ByteBuffer buffer = TextureCache.generateBuffer(images[i]);
			glBindTexture(GL_TEXTURE_2D, texture);

			setTexParameter(interpolate);

			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA8, images[i].getWidth(), images[i].getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

			if (interpolate) {
				glGenerateMipmap(GL_TEXTURE_2D);
			}
		}

		glBindTexture(GL_TEXTURE_2D, 0);
		return texture;
	}

	/**
	 * set Texture Parameter
	 *
	 * @param interpolate
	 */
	private static void setTexParameter(boolean interpolate) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
		if (interpolate) {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		} else {
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		}
	}

	/**
	 * get WrapS Method
	 *
	 * @return wrapS
	 */
	public static int getWrapS() {
		return wrapS;
	}

	/**
	 * set WrapS method
	 *
	 * @param wrapS
	 *            new method
	 */
	public static void setWrapS(int wrapS) {
		CubeMap.wrapS = wrapS;
	}

	/**
	 * get WrapT Method
	 *
	 * @return wrapT
	 */
	public static int getWrapT() {
		return wrapT;
	}

	/**
	 * set WrapT method
	 *
	 * @param wrapT
	 *            new method
	 */
	public static void setWrapT(int wrapT) {
		CubeMap.wrapT = wrapT;
	}
}
