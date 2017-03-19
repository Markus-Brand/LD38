package mbeb.opengldefault.scene.entities;

import org.joml.*;

import mbeb.opengldefault.camera.*;

/**
 * Adapter class that enables a {@link ICamera} to be interpreted as {@link Entity}
 *
 * @author Markus
 */
public class CameraEntity extends Entity {

	private final ICamera camera;

	public CameraEntity(final ICamera camera) {
		this.camera = camera;
	}

	@Override
	public Vector3f getPosition() {
		return camera.getPosition();
	}

	@Override
	public Vector3f getDirection() {
		return camera.getViewDirection();
	}

	@Override
	public void setPosition(final Vector3f position) {
		camera.setPosition(position);
	}

	@Override
	public void setDirection(final Vector3f direction) {
		camera.setViewDirection(direction);
	}

}
