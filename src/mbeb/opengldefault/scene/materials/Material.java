package mbeb.opengldefault.scene.materials;

import java.awt.*;
import java.awt.image.BufferedImage;

import mbeb.opengldefault.gl.buffer.GLBufferWritable;
import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.gl.texture.Texture2DArray;

/**
 * the look of geometry inside a scene
 *
 * The 4 layers are defined as:
 * (diffuseAlpha / specular(empty) / emission(empty) / normalBump)
 */
public class Material implements GLBufferWritable {

	/** the exponent for specular lights: controls how hard a material feels */
	private int shininess = 64;

	/** the texture for this material: multiple layers defining different properties of a material */
	private final Texture2DArray myTextures;

	/**
	 * create a new material based on a texture
	 * @param texture
	 */
	public Material(Texture2DArray texture) {
		myTextures = texture;
		texture.whileBound((Texture t) -> t.setInterpolates(true) && t.generateMipmaps() && t.setWrapModeR(Texture.WrapMode.CLAMP_TO_BORDER) && t.setBorderColor(Color.BLACK));
	}

	/**
	 * construct a new Material based on some images
	 * @param images the material components (all in same resolution)
	 */
	public Material(BufferedImage[] images) {
		this(new Texture2DArray(images));
	}

	/**
	 * construct a new Material based on images in a resource path.
	 * The images are named <code>path/[imageNumber].extension</code>
	 * @param path the path to the material
	 * @param extension the image file extension
	 * @param amount how many layers this material should use
	 */
	public Material(String path, String extension, int amount) {
		this(Texture.loadBufferedImages(path, extension, amount));
	}

	/**
	 * overloaded constructor with "png" as file extension
	 * @param path the path to the material
	 * @param amount how many layers this material should use
	 * @see #Material(String, String, int)
	 */
	public Material(String path, int amount) {
		this(path, "png", amount);
	}

	/**
	 * @return the shininess of this material
	 */
	public int getShininess() {
		return shininess;
	}

	/**
	 * set a new shininess value for this material (updated on next usage)
	 * @param shininess
	 */
	public void setShininess(int shininess) {
		this.shininess = shininess;
	}

	@Override
	public void writeTo(GLBufferWriter writer) {
		writer.write(myTextures).write(shininess);
	}

	/**
	 * upload this Material-object to a specified uniform of a shader
	 * 
	 * @param program the ShaderProgram to update
	 * @param name the name of the Material-uniform (see modules/Struct_Material.glsl)
	 */
	public void setUniform(ShaderProgram program, String name) {
		program.setUniform(name + ".textureLayers", myTextures);
		program.setUniform(name + ".shininess", getShininess());
	}

	/**
	 * prepare this material for usage
	 */
	public void bind() {
		myTextures.bind();
	}

	/**
	 * call this after you are done using this material
	 */
	public void unbind() {
		myTextures.unbind();
	}
}
