package mbeb.opengldefault.scene.materials;

import mbeb.opengldefault.rendering.textures.Texture;
import org.joml.Vector4f;

import java.awt.*;

/**
 * One Attribute of a material. It is backed by a texture XOR by a vec3 (color/normal/...)
 */
public class MaterialComponent {
	
	private Vector4f value = null;
	private Texture texValue = null;
	
	/**
	 * create a new MaterialComponent with a uniform value
	 * @param value the color to set this component to
	 */
	public MaterialComponent(Color value) {
		setValue(value);
	}
	
	/**
	 * create a new MaterialComponent with a uniform value
	 */
	public MaterialComponent() {
		this(new Vector4f());
	}
	
	/**
	 * create a new MaterialComponent with a uniform value
	 * @param value the vec3 to set this component to
	 */
	public MaterialComponent(Vector4f value) {
		setValue(value);
	}
	
	/**
	 * create a new MaterialComponent that uses a texture
	 * @param texValue the Texture to use
	 */
	public MaterialComponent(Texture texValue) {
		setTexValue(texValue);
	}
	
	/**
	 * @return whether this Component has one object-wide value (instead of a texture)
	 */
	public boolean isSingleValue() {
		return value != null;
	}
	
	/**
	 * set a uniform color for this component (removes any previous texture)
	 * @param value the new color
	 */
	public void setValue(Color value) {
		setValue(new Vector4f(value.getRed() / 255f, value.getGreen() / 255f, value.getBlue() / 255f, value.getAlpha() / 255f));
	}
	
	/**
	 * set a uniform value for this component (removes any previous texture)
	 * @param value the new value
	 */
	public void setValue(Vector4f value) {
		this.value = value;
		this.texValue = null;
	}
	
	
	/**
	 * set a texture for this component (removes any previous global value)
	 * @param texValue the new texture to use
	 */
	public void setTexValue(Texture texValue) {
		this.texValue = texValue;
		this.value = null;
	}
	
	/**
	 * only valid if {@link #isSingleValue()} == true
	 * @return the value of this MaterialComponent
	 */
	public Vector4f getValue() {
		return value;
	}
	
	/**
	 * only valid if {@link #isSingleValue()} == false
	 * @return the texture that represents this MaterialComponent
	 */
	public Texture getTexture() {
		return texValue;
	}
	
}
