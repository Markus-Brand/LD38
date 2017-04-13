package mbeb.opengldefault.rendering.renderable;

import org.joml.Matrix4f;

import mbeb.opengldefault.animation.Pose;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * A renderable with its own texture
 */
public class TexturedRenderable implements IRenderable {

	/** Class Name Tag */
	private static final String TAG = "TexturedRenderable";

	public static Texture2D loadModelTexture(String path) {
		Texture2D loaded = new Texture2D(path);
		loaded.whileBound((Texture texture) -> {
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
	 * 
	 * @param shader
	 */
	@Override
	public void render(ShaderProgram shader) {
		texture.bind();
		shader.setUniform("u_texture", texture);
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
