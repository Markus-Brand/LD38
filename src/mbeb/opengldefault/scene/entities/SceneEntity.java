package mbeb.opengldefault.scene.entities;

import mbeb.opengldefault.scene.SceneObject;

import org.joml.Vector3f;

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
		return sceneObject.getTransformation().getRotation().getEulerAnglesXYZ(new Vector3f());
	}

	@Override
	public void setDirection(Vector3f direction) {
		sceneObject.getTransformation().getRotation().rotationTo(new Vector3f(0, 0, -1), direction);
	}

}
