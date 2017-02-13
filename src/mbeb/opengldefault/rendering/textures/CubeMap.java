package mbeb.opengldefault.rendering.textures;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glUniform1i;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.shader.Shader;
import static org.lwjgl.opengl.GL13.*;

public class CubeMap extends Texture {

	private static final String TAG = "CubeMap";

	public CubeMap(int textureHandle) {
		super(textureHandle);
	}

	public CubeMap(String path) {
		this(TextureCache.loadCubeMap(path));
	}

	/**
	 * use "u_cubeMap" as default texture uniform
	 *
	 * @param shader
	 *            the shader to alter
	 * @see #bind(Shader, String)
	 */
	@Override
	public void bind(Shader shader) {
		bind(shader, "u_cubeMap");
	}

	/**
	 * bind this objects texture to the specified uniform
	 *
	 * @param shader
	 *            the shader to alter
	 * @param uniformName
	 *            the name of the uniform to adjust
	 */
	@Override
	public void bind(Shader shader, String uniformName) {
		glBindTexture(GL_TEXTURE_CUBE_MAP, getTextureHandle());
		GLErrors.checkForError(TAG, "glBindTexture");
		glUniform1i(shader.getUniform(uniformName), getTextureHandle());
		GLErrors.checkForError(TAG, "glUniform1i");
	}
}
