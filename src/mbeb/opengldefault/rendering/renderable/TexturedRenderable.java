package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * A renderable with its own shader and texture
 */
public class TexturedRenderable implements IRenderable {

	/** Class Name Tag */
	private static final String TAG = "TexturedRenderable";

	/** a reanderable to be wrapped and rendered */
	private IRenderable renderable;
	/** a texture to bind before rendering */
	private Texture texture;

	public TexturedRenderable(IRenderable renderable, Texture texture) {
		this.renderable = renderable;
		this.texture = texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Texture getTexture() {
		return texture;
	}

	@Override
	public void render(Shader shader) {
		getTexture().bind(shader);
		renderable.render(shader);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return renderable.getBoundingBox();
	}

}
