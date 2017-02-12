package mbeb.opengldefault.animation;

import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * a mesh that has a skeleton
 */
public class AnimatedMesh implements IRenderable {
	
	private VAORenderable mesh;

	public AnimatedMesh(VAORenderable mesh) {
		this.mesh = mesh;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return mesh.getBoundingBox();
	}

	@Override
	public void render(Shader shader) {
		mesh.render(shader);
	}
}
