package mbeb.opengldefault.rendering.textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

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

	/**
	 * load the image provided at <code>path</code>
	 *
	 * @param path
	 *            where to load the image from
	 */
	public Texture(String path) {
		this(TextureCache.loadTexture(path));
	}

	/**
	 * create a new instace with an already loaded OpenGL-Texture
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
		glBindTexture(GL_TEXTURE_2D, textureHandle);
		GLErrors.checkForError(TAG, "glBindTexture");
		shader.setUniform(uniformName, textureHandle);
		GLErrors.checkForError(TAG, "glUniform1i");
	}

	public int getTextureHandle() {
		return textureHandle;
	}
}
