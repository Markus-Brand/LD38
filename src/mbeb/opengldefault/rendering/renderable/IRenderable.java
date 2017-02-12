package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * something that can be rendered into a scene
 */
public interface IRenderable {
	void render(Shader shader);
	
	BoundingBox getBoundingBox();
}
