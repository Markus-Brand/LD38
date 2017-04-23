package mbeb.opengldefault.scene.behaviour;

import org.joml.Vector2f;

public interface HeightSource {
	/**
	 * @param worldSpaceCoordinate
	 *            X and Z Coordinates!!!!
	 * @return height at the position
	 */
	public float getHeight(Vector2f worldSpaceCoordinate);
}
