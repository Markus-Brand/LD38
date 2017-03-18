package mbeb.opengldefault.scene.entities;

import org.joml.*;

import mbeb.opengldefault.scene.*;

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
		Matrix4f inverseMatrix = sceneObject.getParentGlobalTranform().asMatrix().invert(new Matrix4f());
		sceneObject.getTransformation().setPosition(inverseMatrix.transform(new Vector4f(position, 1)));
	}

	@Override
	public Vector3f getDirection() {
		return sceneObject.getTransformation().getRotation().transform(new Vector3f(1, 0, 0));
	}

	@Override
	public void setDirection(Vector3f direction) {
		Vector3f xzPlane = new Vector3f(direction.x, 0, direction.z).normalize();

		Quaternionf rotationYaw = new Quaternionf().rotateTo(new Vector3f(1, 0, 0), xzPlane);
		Quaternionf rotationPitch = new Quaternionf().rotateTo(xzPlane, direction);

		sceneObject.getTransformation().setRotation(rotationPitch.mul(rotationYaw));
	}

}
