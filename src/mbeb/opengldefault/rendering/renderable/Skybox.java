package mbeb.opengldefault.rendering.renderable;

import static org.lwjgl.opengl.GL11.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.rendering.textures.*;

/**
 * Uses a {@link CubeMap} to render a Skybox with the Skybox {@link Shader}
 *
 * @author Markus
 */
public class Skybox {

	private final CubeMap cubeMap;
	private final IRenderable skyboxRenderable;
	private final Shader shader;

	/**
	 * Constructor for a Skybox
	 *
	 * @param texturePath
	 *            path for the skybox textures relative to the textures folder
	 */
	public Skybox(final String texturePath) {
		cubeMap = new CubeMap(texturePath);
		shader = new Shader("skybox.vert", "skybox.frag");
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
		glDepthFunc(GL_LEQUAL);
		cubeMap.bind(shader);
		skyboxRenderable.render(shader);
		glDepthFunc(GL_LESS);
		glDepthMask(true);
	}
}
