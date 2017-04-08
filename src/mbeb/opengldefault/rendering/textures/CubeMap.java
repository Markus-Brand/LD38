package mbeb.opengldefault.rendering.textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import mbeb.opengldefault.gl.texture.TextureLoader;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.shader.*;

/**
 * Subclass of {@link Texture}, that stores a GL_TEXTURE_CUBE_MAP instead of a GL_TEXTURE_2D
 * 
 * @author Markus
 */
public class CubeMap extends Texture {

	private static final String TAG = "CubeMap";

	public CubeMap(int textureHandle) {
		super(textureHandle);
	}

	public CubeMap(String path) {
		this(TextureLoader.loadCubeMap(path));
	}

	/**
	 * use "u_cubeMap" as default texture uniform
	 *
	 * @param shader
	 *            the shader to alter
	 * @see #bind(ShaderProgram, String)
	 */
	@Override
	public void bind(ShaderProgram shader) {
		bind(shader, "u_cubeMap");
	}

	@Override
	public void bind(ShaderProgram shader, String uniformName) {
		glBindTexture(GL_TEXTURE_CUBE_MAP, getTextureHandle());
		GLErrors.checkForError(TAG, "glBindTexture");
		shader.setUniform(uniformName, getTextureHandle());
		GLErrors.checkForError(TAG, "glUniform1i");
	}
}
