package mbeb.opengldefault.rendering;

import mbeb.opengldefault.main.GLErrors;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.glUniform1i;

/**
 * A Texture that can binded to a uniform
 */
public class Texture {
	
	private static final String TAG = "Texture";
	
	private int textureHandle;

	public Texture(String path) {
		textureHandle = TextureUtils.loadTexture(path);
	}

	public Texture(int textureHandle) {
		this.textureHandle = textureHandle;
	}
	
	public void bind(Shader shader) {
		bind(shader, "u_texture");
	}
	public void bind(Shader shader, String uniformName) {
		glBindTexture(GL_TEXTURE_2D, textureHandle);
		GLErrors.checkForError(TAG, "glBindTexture");
		glUniform1i(shader.getUniform(uniformName), textureHandle);
		GLErrors.checkForError(TAG, "glUniform1i");
	}
	
}
