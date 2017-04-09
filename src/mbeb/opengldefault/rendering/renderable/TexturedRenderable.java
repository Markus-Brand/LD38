package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.gl.texture.Texture;
import org.joml.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

/**
 * A renderable with its own texture
 */
public class TexturedRenderable implements IRenderable {

	/** Class Name Tag */
	private static final String TAG = "TexturedRenderable";

	/** a renderable to be wrapped and rendered */
	private IRenderable renderable;
	/** a texture to bind before rendering */
	private Texture texture;

	public TexturedRenderable(IRenderableHolder renderable, Texture texture) {
		this.renderable = renderable.getRenderable();
		this.texture = texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Texture getTexture() {
		return texture;
	}

	/**
	 * binds the texture and then passes the call to the wrapped IRenderable
	 * @param shader
	 */
	@Override
	public void render(ShaderProgram shader) {
		shader.setUniform("u_texture", texture, true);
		renderable.render(shader);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return renderable.getBoundingBox();
	}

	@Override
	public void update(double deltaTime) {
		renderable.update(deltaTime);
	}

	@Override
	public boolean hasAnimations() {
		return renderable.hasAnimations();
	}

	@Override
	public Pose getCurrentPose() {
		return renderable.getCurrentPose();
	}

	@Override
	public Matrix4f getTransform() {
		return renderable.getTransform();
	}

}
