package mbeb.opengldefault.rendering.renderable;

import static org.lwjgl.opengl.GL11.*;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.rendering.textures.CubeMap;

public class Skybox {

	private CubeMap cubeMap;
	private IRenderable skyboxRenderable;
	private Shader shader;

	public Skybox(String texturePath) {
		cubeMap = new CubeMap(texturePath);
		shader = new Shader("skybox.vert", "skybox.frag");
		shader.addUniformBlockIndex(1, "Matrices");
		skyboxRenderable = StaticMeshes.getSkybox();
	}

	public CubeMap getTexture() {
		return cubeMap;
	}

	public void render(ICamera camera) {
		shader.use();

		glDepthMask(false);
		glDepthFunc(GL_LEQUAL);
		cubeMap.bind(shader);
		skyboxRenderable.render(shader);
		glDepthFunc(GL_LESS);
		glDepthMask(true);
	}
}
