package mbeb.opengldefault.light;

import java.awt.*;

import org.joml.*;

/**
 * @author Erik + Merlin + Markus :)
 */

public abstract class Light {

	protected Vector3f color;
	/**
	 * If my data is changed, this flag will signal it and the next LightManager update cycle will write the changes to
	 * the UBO
	 */
	private boolean dirty = true;
	/** When this flag is set, the next LightManager update cycle will delete me from the UBO */
	private boolean markedForRemoval = false;

	public Light(final Vector3f color) {
		setColor(color);
	}

	public Light(final Color color) {
		setColor(color);
	}

	/**
	 * transforms <i>color</i> with range [0, 255] to Vec3 with range [0, 1]
	 * 
	 * @param color
	 *            with rgb values in [0, 255]^3
	 * @return Vector3f with rgb values [0, 1]^3
	 */
	public static Vector3f vectorFromColor(final Color color) {
		final float maxValue = 255.0f;
		return new Vector3f(color.getRed() / maxValue, color.getGreen() / maxValue, color.getBlue() / maxValue);
	}

	/**
	 * sets my color to <i>color</i> and sets my dirty flag
	 * 
	 * @param color
	 */
	public void setColor(final Vector3f color) {
		this.color = color;
		setDirty();
	}

	/**
	 * sets my color to <i>color</i> and sets my dirty flag
	 * 
	 * @param color
	 */
	public void setColor(final Color color) {
		setColor(vectorFromColor(color));
	}

	/**
	 * @return my color as Vector3f
	 */
	public Vector3f getColor() {
		return color;
	}

	public abstract int getBlockSize();

	public abstract float[] getData();

	/**
	 * @return the status of my dirty flag
	 */
	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * sets the status of my dirty flag to true <br>
	 * consequences: my status in the UBO will be updated
	 */
	public void setDirty() {
		this.dirty = true;
	}

	/**
	 * sets the status of my dirty flag to false
	 */
	public void setClean() {
		this.dirty = false;
	}

	/**
	 * @return the status of my "marked for removal" flag
	 */
	public boolean shouldBeRemoved() {
		return markedForRemoval;
	}

	/**
	 * sets the status of my "marked for removal" flag to true <br>
	 * consequences: my data in the UBO will be deleted
	 */
	public void remove() {
		markedForRemoval = true;
	}

	/**
	 * sets the status of my "marked for removal" flag to false
	 */
	public void cancelRemove() {
		markedForRemoval = false;
	}

}
