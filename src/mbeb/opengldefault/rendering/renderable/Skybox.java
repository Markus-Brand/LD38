package mbeb.opengldefault.rendering.renderable;

import static org.lwjgl.opengl.GL11.*;

import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.shader.UBOManager;
import mbeb.opengldefault.gl.texture.CubeMap;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.scene.Scene;

/**
 * Uses a {@link CubeMap} to render a Skybox with the Skybox {@link ShaderProgram}
 *
 * @author Markus
 */
public class Skybox {

	private static final String TAG = "Skybox";

	private final CubeMap cubeMap;
	private Camera camera;
	private final IRenderable skyboxRenderable;
	private final ShaderProgram shader;

	/**
	 * Constructor for a Skybox
	 *
	 * @param texturePath
	 *            path for the skybox textures relative to the textures folder
	 */
	public Skybox(final String texturePath) {
		this(texturePath, "jpg");
	}

	/**
	 * Constructor for a Skybox
	 *
	 * @param texturePath
	 *            path for the skybox textures relative to the textures folder
	 * @param extension
	 *            the file extension for the images
	 */
	public Skybox(final String texturePath, String extension) {
		cubeMap = new CubeMap(texturePath, extension);
		cubeMap.whileBound(texture -> cubeMap.setWrapMode(Texture.WrapMode.CLAMP_TO_EDGE)
				&& cubeMap.setInterpolates(true) && cubeMap.setBaseLevel(0) && cubeMap.setMaxLevel(0));
		shader = new ShaderProgram("skybox.vert", "skybox.frag");
		shader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
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
	public void render(Camera camera) {
		shader.use();

		glDepthMask(false);
		GLErrors.checkForError(TAG, "glDepthMask");
		glDepthFunc(GL_LEQUAL);
		GLErrors.checkForError(TAG, "glDepthFunc");
		cubeMap.bind();
		shader.setUniform("u_cubeMap", cubeMap);
		shader.setUniform("skyboxView", camera.getSkyboxView());
		skyboxRenderable.render(shader);
		glDepthFunc(GL_LESS);
		GLErrors.checkForError(TAG, "glDepthFunc");
		glDepthMask(true);
		GLErrors.checkForError(TAG, "glDepthMask");
	}
}
