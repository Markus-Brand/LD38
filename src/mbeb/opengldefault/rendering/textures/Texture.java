package mbeb.opengldefault.rendering.textures;

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.shader.Shader;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glUniform1i;

/**
 * A Texture that can bind itself to a uniform for a given {@link Shader}
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
	 * @see bind(Shader, String)
	 */
	public void bind(Shader shader) {
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
	public void bind(Shader shader, String uniformName) {
		glBindTexture(GL_TEXTURE_2D, textureHandle);
		GLErrors.checkForError(TAG, "glBindTexture");
		glUniform1i(shader.getUniform(uniformName), textureHandle);
		GLErrors.checkForError(TAG, "glUniform1i");
	}

	public int getTextureHandle() {
		return textureHandle;
	}
}
