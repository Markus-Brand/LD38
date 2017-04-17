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
	/** the name of the uniform for the material */
	private final String uniformName;
	
	public MaterialRenderable(IRenderableHolder renderable, Material material) {
		this(renderable, material, "material");
	}
	
	public MaterialRenderable(IRenderableHolder renderable, Material material, String uniformName) {
		this.renderable = renderable.getRenderable();
		this.material = material;
		this.uniformName = uniformName;
	}
	
	/**
	 * set a new Material object that should get used for rendering instead of the old one
	 * @param material
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	@Override
	public IRenderable withMaterial(Material material) {
		setMaterial(material);
		return this;
	}
	
	/**
	 * binds the material and then passes the call to the wrapped IRenderable
	 *
     * @param shader
	 */
	@Override
	public void render(ShaderProgram shader) {
		render(shader, true);
	}
	
	/**
	 * render this renderable with material or not.
	 *
	 * @param shader
	 * @param useMaterial whether to bind the material for this render call or not
	 */
	public void render(ShaderProgram shader, boolean useMaterial) {
		if (useMaterial) {
			material.bind();
			material.setUniform(shader, uniformName);
			renderable.render(shader);
			material.unbind();
		} else {
			renderable.render(shader);
		}
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
