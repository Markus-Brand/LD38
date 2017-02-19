package mbeb.opengldefault.scene.entities;

import mbeb.opengldefault.scene.SceneObject;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Adapter class that enables a {@link SceneObject} to be interpreted as {@link Entity}
 * 
 * @author Markus
 */
public class SceneEntity extends Entity {

	private SceneObject sceneObject;

	public SceneEntity(SceneObject sceneObject) {
		this.sceneObject = sceneObject;
	}

	public SceneObject getSceneObject() {
		return sceneObject;
	}

	@Override
	public Vector3f getPosition() {
		return sceneObject.getPosition();
	}

	@Override
	public void setPosition(Vector3f position) {
		Vector3f delta = position.sub(getPosition(), new Vector3f());
		sceneObject.getTransformation().getPosition().add(delta);
	}

	@Override
	public Vector3f getDirection() {
		return sceneObject.getTransformation().getRotation().transform(new Vector3f(1, 0, 0));
	}

	@Override
	public void setDirection(Vector3f direction) {
		//TDOD: Make correct
		Vector3f xzPlane = new Vector3f(direction.x, 0, direction.z).normalize();

		float pitch = xzPlane.angle(direction);
		float yaw = new Vector3f(1, 0, 0).angle(xzPlane);
		Quaternionf rotationYaw = new Quaternionf().rotateTo(new Vector3f(1, 0, 0), xzPlane);
		Quaternionf rotationPitch = new Quaternionf().rotateTo(xzPlane, direction);

		sceneObject.getTransformation().setRotation(rotationPitch.mul(rotationYaw));
	}

}
