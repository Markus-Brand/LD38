package mbeb.opengldefault.scene.entities;

import org.joml.Vector3f;

import mbeb.opengldefault.camera.Camera;

/**
 * Adapter class that enables a {@link Camera} to be interpreted as {@link Entity}
 *
 * @author Markus
 */
public class CameraEntity extends Entity {

	private final Camera camera;
	private Vector3f viewDirection;

	public CameraEntity(final Camera camera) {
		this.camera = camera;
		viewDirection = camera.getCenter().sub(camera.getEye(), new Vector3f());
	}

	@Override
	public Vector3f getPosition() {
		return camera.getEye();
	}

	@Override
	public Vector3f getDirection() {
		return viewDirection;
	}

	@Override
	public void setPosition(final Vector3f position) {
		camera.setEye(position);
		camera.setCenter(position.add(viewDirection, new Vector3f()));
	}

	@Override
	public void setDirection(final Vector3f direction) {
		this.viewDirection = direction;
		camera.setCenter(getPosition().add(direction, new Vector3f()));
	}

}
