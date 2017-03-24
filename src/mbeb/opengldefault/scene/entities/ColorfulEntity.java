package mbeb.opengldefault.scene.entities;

import java.awt.*;

import org.joml.*;

/**
 * Interface class to make an {@link Entity} colorful
 *
 * @author Merlin (and Markus but in case of blaming ask him and only him :)
 */
public interface ColorfulEntity {

	/**
	 * @return my color
	 */
	public Vector3f getColor();

	/**
	 * @param color
	 *            my new color
	 */
	public void setColor(Vector3f color);

	/**
	 * @param color
	 *            my new color
	 */
	public void setColor(Color color);
}