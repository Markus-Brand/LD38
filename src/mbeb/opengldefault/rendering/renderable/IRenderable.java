package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

/**
 * something that can be rendered into a scene
 */
public interface IRenderable {
	void render(Shader shader);

	BoundingBox getBoundingBox();
}
