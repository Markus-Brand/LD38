package mbeb.opengldefault.scene.entities;

import mbeb.opengldefault.camera.ICamera;

import org.joml.Vector3f;

public class CameraEntity extends Entity {

	private ICamera camera;

	public CameraEntity(ICamera cam) {
		this.camera = cam;
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
	public void setPosition(Vector3f position) {
		camera.setPosition(position);
	}

	@Override
	public void setDirection(Vector3f direction) {
		camera.setViewDirection(direction);
	}

}
