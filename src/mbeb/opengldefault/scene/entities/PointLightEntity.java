package mbeb.opengldefault.scene.entities;

import java.awt.*;

import org.joml.*;

import mbeb.opengldefault.light.*;

/**
 * {@link Entity} for a {@link PointLight}
 *
 * @author Merlin (and Markus and Erik but in case of blaming ask him and only him :)
 */
public class PointLightEntity extends Entity implements ColorfulEntity {

	/** my {@link PointLight} */
	private PointLight pointlight;

	/**
	 * @param pointlight
	 *            my {@link PointLight}
	 */
	public PointLightEntity(final PointLight pointlight) {
		this.pointlight = pointlight;
	}

	/**
	 * @return my color
	 */
	@Override
	public Vector3f getColor() {
		return pointlight.getColor();
	}

	@Override
	public void setColor(final Vector3f color) {
		pointlight.setColor(color);
	}

	@Override
	public void setColor(final Color color) {
		pointlight.setColor(color);
	}

	@Override
	public Vector3f getPosition() {
		return pointlight.getPosition();
	}

	/**
	 * PointLight is <b>omnidirectional</b> so this method always returns (1,0,0)
	 */
	@Override
	public Vector3f getDirection() {
		return new Vector3f(1, 0, 0);
	}

	@Override
	public void setPosition(final Vector3f position) {
		pointlight.setPosition(position);
	}

	/**
	 * PointLight is <b>omnidirectional</b> so this method has no <b>effect</b>
	 */
	@Override
	public void setDirection(final Vector3f direction) {
		//just ignore the value
	}

	public PointLight getLight() {
		return pointlight;
	}

	public void setPointlight(PointLight pointlight) {
		this.pointlight = pointlight;
	}
}
