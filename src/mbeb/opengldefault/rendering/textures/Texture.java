package mbeb.opengldefault.rendering.textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import mbeb.opengldefault.gl.texture.TextureLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.shader.*;

/**
 * A Texture that can bind itself to a uniform for a given {@link ShaderProgram}
 */
public class Texture {

	private static final String TAG = "Texture";

	/** OpenGL-handle id for the texture */
	private int textureHandle;

	public Texture(int width, int height) {
		this(TextureLoader.loadTexture(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), false,
				GL_CLAMP_TO_EDGE));
	}

	/**
	 * Generate OpenGL Texture from BufferedImage
	 * @param image the input BufferedImage
	 */
	public Texture(BufferedImage image) {
		this(TextureLoader.loadTexture(image, false, GL_CLAMP_TO_EDGE,
				GL_CLAMP_TO_EDGE));
	}

	/**
	 * load the image provided at <code>path</code>
	 *
	 * @param path
	 *            where to load the image from
	 */
	public Texture(String path) {
		this(TextureLoader.loadTexture(path));
	}

	/**
	 * create a new instance with an already loaded OpenGL-Texture
	 *
	 * @param textureHandle
	 *            a valid texture handle
	 */
	public Texture(int textureHandle) {
		this.textureHandle = textureHandle;
	}

	/**
	 * use "u_texture" as default texture uniform
	 *
	 * @param shader
	 *            the shader to alter
	 * @see bind( ShaderProgram , String)
	 */
	public void bind(ShaderProgram shader) {
		bind(shader, "u_texture");
	}

	/**
	 * bind this objects texture to the specified uniform
	 *
	 * @param shader
	 *            the shader to alter
	 * @param uniformName
	 *            the name of the uniform to adjust
	 */
	public void bind(ShaderProgram shader, String uniformName) {
		GL13.glActiveTexture(GL_TEXTURE0 + textureHandle);
		GLErrors.checkForError(TAG, "glActiveTexture");
		glBindTexture(GL_TEXTURE_2D, textureHandle);
		GLErrors.checkForError(TAG, "glBindTexture");
		shader.setUniform(uniformName, textureHandle);
		GLErrors.checkForError(TAG, "glUniform1i");
	}

	public int getTextureHandle() {
		return textureHandle;
	}

	public void setPixel(int x, int y, Color color) {
		GL13.glActiveTexture(GL_TEXTURE0 + textureHandle);
		glBindTexture(GL_TEXTURE_2D, textureHandle);
		GLErrors.checkForError(TAG, "glBindTexture");
		ByteBuffer buffer = BufferUtils.createByteBuffer(4);
		buffer.put((byte) color.getRed());
		buffer.put((byte) color.getGreen());
		buffer.put((byte) color.getBlue());
		buffer.put((byte) color.getAlpha());
		buffer.flip();
		glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		GLErrors.checkForError(TAG, "glTexSubImage2D");
	}
}
