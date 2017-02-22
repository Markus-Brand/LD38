package mbeb.opengldefault.light;

import java.awt.*;

import org.joml.*;

/**
 * @author Erik + Merlin + Markus :)
 */

public abstract class Light {

	protected Vector3f color;
	private boolean dirty = true; //TODO: check whether true or false is better

	public Light(final Vector3f color) {
		setColor(color);
	}

	public Light(final Color color) {
		setColor(color);
	}

	public static Vector3f vectorFromColor(final Color color) {
		final float maxValue = 255.0f;
		return new Vector3f(color.getRed() / maxValue, color.getGreen() / maxValue, color.getBlue() / maxValue);
	}

	public void setColor(final Vector3f color) {
		this.color = color;
		setDirty();
	}

	public void setColor(final Color color) {
		setColor(vectorFromColor(color));
	}

	public Vector3f getColor() {
		return color;
	}

	public abstract int getBlockSize();

	public abstract float[] getData();

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty() {
		this.dirty = true;
	}

	public void setClean() {
		this.dirty = false;
	}
}
