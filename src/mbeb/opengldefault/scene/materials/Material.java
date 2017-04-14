package mbeb.opengldefault.scene.materials;

import mbeb.opengldefault.gl.buffer.GLBufferWritable;
import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.gl.texture.Texture2DArray;

import java.awt.image.BufferedImage;

/**
 * the look of geometry inside a scene
 */
public class Material implements GLBufferWritable {
	/**
	 * diffuse / specular / emission / normalBump
	 */
	protected static final int MATERIAL_LAYERS = 4;
	
	private int shininess = 64; //todo should this also be mappable?

	private final Texture2DArray myTextures;
	
	public Material(Texture2DArray texture) {
		myTextures = texture;
	}

	public Material(BufferedImage[] images) {
		this(new Texture2DArray(images, MATERIAL_LAYERS));
	}

	public Material(String path, String extension, int amount) {
		this(Texture.loadBufferedImages(path, extension, amount));
	}
	
	public int getShininess() {
		return shininess;
	}
	
	public void setShininess(int shininess) {
		this.shininess = shininess;
	}

	@Override
	public void writeTo(GLBufferWriter writer) {
		writer
			.write(myTextures)
			.write(shininess);
	}
}
