package mbeb.opengldefault.scene.materials;

import org.joml.Vector4f;

/**
 * the look of geometry inside a scene
 */
public class Material {
	
	/** the overall color of the object (used for ambient and diffuse shading) and the specularity in the alpha channel */
	private final MaterialComponent diffuseSpec;
	
	/** an emission color for the object (optional) */
	private MaterialComponent emission = null;
	/** a normal map for shading, with a height map in the alpha channel (optional) */
	private MaterialComponent normalBump = null;
	
	private int shininess = 64; //todo should this also be mappable?
	
	public Material() {
		this(new MaterialComponent(new Vector4f(0.5f)));
	}
	
	public Material(MaterialComponent diffuse) {
		this.diffuseSpec = diffuse;
		this.shininess = 64;
	}
	
	public int getShininess() {
		return shininess;
	}
	
	public void setShininess(int shininess) {
		this.shininess = shininess;
	}
	
	public MaterialComponent getDiffuseSpec() {
		return diffuseSpec;
	}
	
	public MaterialComponent getEmission() {
		if (emission == null) {
			emission = new MaterialComponent();
		}
		return emission;
	}
	
	public MaterialComponent getNormalBump() {
		if (normalBump == null) {
			normalBump = new MaterialComponent();
		}
		return normalBump;
	}
}
