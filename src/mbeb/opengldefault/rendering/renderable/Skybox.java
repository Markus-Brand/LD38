package mbeb.opengldefault.rendering.renderable;

import static org.lwjgl.opengl.GL11.*;

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.gl.shader.*;
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
		cubeMap.whileBound(texture -> {
			boolean success = cubeMap.setWrapMode(Texture.WrapMode.CLAMP_TO_EDGE);
			success = success && cubeMap.setInterpolates(false);
			success = success && cubeMap.setBaseLevel(0);
			return success && cubeMap.setMaxLevel(0);
		});
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
		cubeMap.bind();
		shader.setUniform("u_cubeMap", cubeMap);
		skyboxRenderable.render(shader);
		glDepthFunc(GL_LESS);
		GLErrors.checkForError(TAG, "glDepthFunc");
		glDepthMask(true);
		GLErrors.checkForError(TAG, "glDepthMask");
	}
}
