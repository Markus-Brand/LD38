package mbeb.opengldefault.scene.entities;

import java.awt.*;

import org.joml.*;

import mbeb.opengldefault.light.*;

/**
 * {@link Entity} for a {@link SpotLight}
 *
 * @author Merlin (and Markus and Erik but in case of blaming ask him and only him :)
 */
public class SpotLightEntity extends Entity implements ColorfulEntity {

	/** my {@link SpotLight} */
	private final SpotLight spottlight;

	/**
	 * @param spotlight
	 *            my {@link SpotLight}
	 */
	public SpotLightEntity(final SpotLight spotlight) {
		this.spottlight = spotlight;
	}

	@Override
	public Vector3f getColor() {
		return spottlight.getColor();
	}

	@Override
	public void setColor(final Vector3f color) {
		spottlight.setColor(color);
	}

	@Override
	public void setColor(final Color color) {
		spottlight.setColor(color);
	}

	@Override
	public Vector3f getPosition() {
		return spottlight.getPosition();
	}

	@Override
	public Vector3f getDirection() {
		return spottlight.getDirection();
	}

	@Override
	public void setPosition(final Vector3f position) {
		spottlight.setPosition(position);
	}

	@Override
	public void setDirection(final Vector3f direction) {
		spottlight.setDirection(direction);
	}
}
