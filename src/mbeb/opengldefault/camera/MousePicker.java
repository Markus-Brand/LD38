package mbeb.opengldefault.camera;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.scene.SceneObject;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MousePicker {
	private Vector3f ray;
	private ICamera camera;

	public MousePicker(ICamera camera2) {
		this.camera = camera2;
	}

	public Vector3f getRay() {
		return ray;
	}

	public void update(double deltaTime) {
		calculateRay();
	}

	private void calculateRay() {
		Vector2f mousePos = Mouse.getPos();
		Vector2f normalizedDeviceCoordinates = getNormalizedDeviceCoordinates(mousePos);
		Vector4f clipCoordinates = new Vector4f(normalizedDeviceCoordinates.x, normalizedDeviceCoordinates.y, -1, 1);
		Vector4f eyeSpaceCoordinates = getEyeSpaceCoordinates(clipCoordinates);
		Vector3f worldSpaceCoordinates = getWorldSpaceCoordinates(eyeSpaceCoordinates);
		ray = worldSpaceCoordinates;
	}

	private Vector2f getNormalizedDeviceCoordinates(Vector2f mousePos) {
		return new Vector2f(
				2 * mousePos.x / OpenGLContext.getVideoModeWidth()- 1,
				-(2 * mousePos.y / OpenGLContext.getVideoModeHeight()- 1));
	}

	private Vector3f getWorldSpaceCoordinates(Vector4f eyeSpaceCoordinates) {
		Matrix4f invertedView = camera.getView().invert(new Matrix4f());
		Vector4f worldSpaceCoordinates = invertedView.transform(eyeSpaceCoordinates, new Vector4f());
		return new Vector3f(worldSpaceCoordinates.x, worldSpaceCoordinates.y, worldSpaceCoordinates.z).normalize();
	}

	private Vector4f getEyeSpaceCoordinates(Vector4f clipCoordinates) {
		Matrix4f invertedProjection = camera.getProjection().invert(new Matrix4f());
		Vector4f eyeSpaceCoordinates = invertedProjection.transform(clipCoordinates, new Vector4f());
		return new Vector4f(eyeSpaceCoordinates.x, eyeSpaceCoordinates.y, -1, 0);
	}

	public void searchBBs(SceneObject sceneGraph) {
		if (ray == null || camera.getPosition() == null) {
			return;
		}
		if (sceneGraph.getBoundingBox().intersectsRay(camera.getPosition(), ray)) {
			sceneGraph.getBoundingBox().setColor(new Vector3f(1, 0, 0));
		} else {
			sceneGraph.getBoundingBox().setColor(new Vector3f(0, 1, 0));
		}
		for (SceneObject child : sceneGraph.getSubObjects()) {
			searchBBs(child);
		}
	}
}
