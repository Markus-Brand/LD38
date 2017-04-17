package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.animation.Pose;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.scene.BoundingBox;
import mbeb.opengldefault.scene.materials.Material;
import org.joml.Matrix4f;

/**
 * A decorated Renderable that also uses it's material before rendering
 */
public class MaterialRenderable implements IRenderable{

	/** a renderable to be wrapped and rendered */
	private IRenderable renderable;
	/** a material to bind before rendering */
	private Material material;

	public MaterialRenderable(IRenderableHolder renderable, Material material) {
		this.renderable = renderable.getRenderable();
		this.material = material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	@Override
	public Material getMaterial() {
		return material;
	}
	
	@Override
	public IRenderable withMaterial(Material material) {
		this.material = material;
		return this;
	}
	
	/**
	 * binds the material and then passes the call to the wrapped IRenderable
	 *
	 * @param shader
	 */
	@Override
	public void render(ShaderProgram shader) {
		material.bind();
		material.setUniform(shader, "material");
		renderable.render(shader);
		material.unbind();
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
