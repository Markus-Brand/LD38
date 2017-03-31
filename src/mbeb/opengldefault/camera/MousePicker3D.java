package mbeb.opengldefault.camera;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.scene.SceneObject;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A class for calculating a camera ray for 3D Mouse picking
 * @author Markus
 *
 */
public class MousePicker3D {
	
	/**
	 * The current mouse ray
	 */
	private Vector3f ray;
	
	/**
	 * Current camera
	 */
	private final ICamera camera;

	public MousePicker3D(final ICamera camera) {
		this.camera = camera;
	}

	/**
	 * Getter for the ray
	 * @return the current ray
	 */
	public Vector3f getRay() {
		return ray;
	}

	/**
	 * Updates the ray to match current camera and Mouse position
	 * @param deltaTime time sincce last update
	 */
	public void update(double deltaTime) {
		calculateRay();
	}

	/**
	 * Calcukates the ray
	 */
	private void calculateRay() {
		Vector2f normalizedDeviceCoordinates = Mouse.getNormalizedDeviceCoordinates();
		Vector4f clipCoordinates = new Vector4f(normalizedDeviceCoordinates.x, normalizedDeviceCoordinates.y, -1, 1);
		Vector4f eyeSpaceCoordinates = getEyeSpaceCoordinates(clipCoordinates);
		Vector3f worldSpaceCoordinates = getWorldSpaceCoordinates(eyeSpaceCoordinates);
		ray = worldSpaceCoordinates;
	}

	/**
	 * Converts from eyeSpaceCoordinates to worldSpaceCoordinates
	 * @param eyeSpaceCoordinates ray in eyeSpaceCoordinates
	 * @return ray in worldSpaceCoordinates
	 */
	private Vector3f getWorldSpaceCoordinates(Vector4f eyeSpaceCoordinates) {
		Matrix4f invertedView = camera.getView().invert(new Matrix4f());
		Vector4f worldSpaceCoordinates = invertedView.transform(eyeSpaceCoordinates, new Vector4f());
		return new Vector3f(worldSpaceCoordinates.x, worldSpaceCoordinates.y, worldSpaceCoordinates.z).normalize();
	}
	
	/**
	 * Converts from clipCoordinates to eyeSpaceCoordinates
	 * @param clipCoordinates ray in clipCoordinates
	 * @return ray in eyeSpaceCoordinates
	 */
	private Vector4f getEyeSpaceCoordinates(Vector4f clipCoordinates) {
		Matrix4f invertedProjection = camera.getProjection().invert(new Matrix4f());
		Vector4f eyeSpaceCoordinates = invertedProjection.transform(clipCoordinates, new Vector4f());
		return new Vector4f(eyeSpaceCoordinates.x, eyeSpaceCoordinates.y, -1, 0);
	}

	/**
	 * Traverses a scene and selects all Objects whose BoundingBox intersects the ray
	 * @param currentObject the scene or current SceneObject
	 * @param parentTransform parent transformation to convert from parentSpace to worldSpace
	 */
	public void searchBoundingBoxes(SceneObject currentObject, Matrix4f parentTransform) {
		if (ray == null || camera.getPosition() == null || currentObject.getBoundingBox() == null) {
			return;
		}

		boolean selected = currentObject.getBoundingBox().intersectsRay(camera.getPosition(), ray, parentTransform);

		currentObject.setSelected(selected);

		final Matrix4f transform = parentTransform.mul(currentObject.getTransformation().asMatrix(), new Matrix4f());
		for (SceneObject child : currentObject.getSubObjects()) {
			searchBoundingBoxes(child, transform);
		}
	}
}
