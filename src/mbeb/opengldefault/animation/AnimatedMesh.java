package mbeb.opengldefault.animation;

import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.VAORenderable;

/**
 * a mesh that has a skeleton
 */
public class AnimatedMesh implements IRenderable {
	
	private VAORenderable mesh;

	public AnimatedMesh(VAORenderable mesh) {
		this.mesh = mesh;
	}
	
	@Override
	public void render() {
		mesh.render();
	}
	
}
