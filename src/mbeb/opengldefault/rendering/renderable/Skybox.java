package mbeb.opengldefault.rendering.renderable;

import static org.lwjgl.opengl.GL11.*;

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.gl.texture.*;

/**
 * Uses a {@link CubeMap} to render a Skybox with the Skybox {@link ShaderProgram}
 *
 * @author Markus
 */
public class Skybox {

	private static final String TAG = "Skybox";

	private final CubeMap cubeMap;
	private final IRenderable skyboxRenderable;
	private final ShaderProgram shader;

	/**
	 * Constructor for a Skybox
	 *
	 * @param texturePath
	 *            path for the skybox textures relative to the textures folder
	 */
	public Skybox(final String texturePath) {
		cubeMap = new CubeMap(texturePath);
		shader = new ShaderProgram("skybox.vert", "skybox.frag");
		shader.addUniformBlockIndex(UBOManager.MATRICES);
		skyboxRenderable = StaticMeshes.getCube();
	}

	/**
	 * Getter for the cubeMap
	 *
	 * @return
	 */
	public CubeMap getTexture() {
		return cubeMap;
	}

	/**
	 * Renders the skybox
	 */
	public void render() {
		shader.use();

		glDepthMask(false);
		GLErrors.checkForError(TAG, "glDepthMask");
		glDepthFunc(GL_LEQUAL);
		GLErrors.checkForError(TAG, "glDepthFunc");
		shader.setUniform("u_cubeMap", cubeMap, true);
		skyboxRenderable.render(shader);
		glDepthFunc(GL_LESS);
		GLErrors.checkForError(TAG, "glDepthFunc");
		glDepthMask(true);
		GLErrors.checkForError(TAG, "glDepthMask");
	}
}
