package mbeb.opengldefault.scene;

import mbeb.opengldefault.rendering.Shader;
import mbeb.opengldefault.rendering.Texture;

/**
 * A renderable with its own shader and texture
 */
public class TexturedRenderable implements IRenderable {

	/** a reanderable to be wrapped and rendered */
	private IRenderable renderable;
	/** a shader to render the wrapped renderable with */
	private Shader shader;
	/** a texture to bind before rendering */
	private Texture texture;

	public TexturedRenderable(IRenderable renderable, Texture texture) {
		this(renderable, texture, null);
	}

	public TexturedRenderable(IRenderable renderable, Texture texture, Shader shader) {
		this.renderable = renderable;
		this.shader = shader;
		this.texture = texture;
	}

	/**
	 * get the objects shader (or a default one)
	 * @return 
	 */
	public Shader getShader() {
		if (shader == null) {
			shader = new Shader("basic.vert", "phong.frag");
			shader.addUniformBlockIndex(1, "Matrices");
		}
		return shader;
	}

	/**
	 * set your own shader for this object
	 * @param shader 
	 */
	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Texture getTexture() {
		return texture;
	}

	@Override
	public void render() {
		getShader().use();
		getTexture().bind(getShader());
		renderable.render();
	}

}
