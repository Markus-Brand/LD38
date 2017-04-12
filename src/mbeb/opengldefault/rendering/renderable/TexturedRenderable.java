package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.gl.texture.Texture2D;
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

	public static Texture2D loadModelTexture(String path) {
		Texture2D loaded = new Texture2D(path);
		loaded.whileBound(texture -> {
			boolean success = texture.setWrapMode(Texture.WrapMode.REPEAT);
			success = success && texture.setInterpolates(true);
			return success && texture.generateMipmaps();
		});
		return loaded;
	}

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
