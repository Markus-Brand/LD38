package mbeb.opengldefault.scene.entities;

import java.awt.*;

import org.joml.*;

import mbeb.opengldefault.light.*;

/**
 * {@link Entity} for a {@link DirectionalLight}
 *
 * @author Merlin (and Markus and Erik but in case of blaming ask him and only him :)
 */
public class DirectionalLightEntity extends Entity implements ColorfulEntity {

	/** my {@link DirectionalLight} */
	private final DirectionalLight directionallight;

	/**
	 * @param directionallight
	 *            my {@link DirectionalLight}
	 */
	public DirectionalLightEntity(final DirectionalLight directionallight) {
		this.directionallight = directionallight;
	}

	@Override
	public Vector3f getColor() {
		return directionallight.getColor();
	}

	@Override
	public void setColor(final Vector3f color) {
		directionallight.setColor(color);
	}

	@Override
	public void setColor(final Color color) {
		directionallight.setColor(color);
	}

	/**
	 * {@link DirectionalLight} is <b>everywhere</b> so this method always returns (0,0,0)
	 */
	@Override
	public Vector3f getPosition() {
		return new Vector3f(0);
	}

	@Override
	public Vector3f getDirection() {
		return directionallight.getDirection();
	}

	/**
	 * {@link DirectionalLight} is <b>everywhere</b> so this method has <b>no effect</b>
	 */
	@Override
	public void setPosition(final Vector3f position) {
		//just ignore the value
	}

	@Override
	public void setDirection(final Vector3f direction) {
		directionallight.setDirection(direction);
	}
}
