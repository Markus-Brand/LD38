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
 */
public class Material implements GLBufferWritable {
	/**
	 * diffuseAlpha / specular(Shininess) / emission(AO) / normalBump
	 */
	private static final int MATERIAL_LAYERS = 4;

	private int shininess = 64; //todo should this also be mappable?

	private final Texture2DArray myTextures;

	public Material(Texture2DArray texture) {
		myTextures = texture;
		texture.whileBound((Texture t) -> t.setInterpolates(true) && t.generateMipmaps() && t.setWrapModeR(Texture.WrapMode.CLAMP_TO_BORDER) && t.setBorderColor(Color.BLACK));
	}

	public Material(BufferedImage[] images) {
		this(new Texture2DArray(images));
	}

	public Material(String path, String extension, int amount) {
		this(Texture.loadBufferedImages(path, extension, amount));
	}

	public Material(String path, int amount) {
		this(path, "png", amount);
	}

	public int getShininess() {
		return shininess;
	}

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
	 * @param program
	 * @param name
	 */
	public void setUniform(ShaderProgram program, String name) {
		program.setUniform(name + ".textureLayers", myTextures);
		program.setUniform(name + ".shininess", shininess);
	}

	public void bind() {
		myTextures.bind();
	}

	public void unbind() {
		myTextures.unbind();
	}
}
