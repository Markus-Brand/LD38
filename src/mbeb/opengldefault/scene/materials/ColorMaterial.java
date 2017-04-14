package mbeb.opengldefault.scene.materials;

import org.joml.Vector4f;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A Material with only one-colored layers
 */
public class ColorMaterial extends Material {

	public ColorMaterial(Vector4f... colors) {
		this(toColors(colors));
	}

	private static Color[] toColors(Vector4f[] vectors) {
		Color[] colors = new Color[vectors.length];
		for (int v = 0; v < vectors.length; v++) {
			Vector4f vec = vectors[v];
			colors[v] = new Color(vec.x(), vec.y(), vec.z(), vec.w());
		}
		return colors;
	}

	public ColorMaterial(Color... colors) {
		this(createTextures(colors));
	}

	private static BufferedImage[] createTextures(Color[] colors) {
		BufferedImage[] images = new BufferedImage[colors.length];
		for (int l = 0; l < colors.length; l++) {
			BufferedImage layer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			layer.setRGB(0, 0, colors[l].getRGB());
			images[l] = layer;
		}
		return images;
	}

	private ColorMaterial(BufferedImage[] images) {
		super(images);
	}
}
