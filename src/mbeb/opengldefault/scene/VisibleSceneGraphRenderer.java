package mbeb.opengldefault.scene;

import mbeb.opengldefault.camera.*;

import org.joml.*;

/**
 * only renders the visible part of the sceneGraph
 */
public class VisibleSceneGraphRenderer extends SceneGraphRenderer {

	private static final float MIN_SCREEN_AREA = 0.00001f;

	public VisibleSceneGraphRenderer(final SceneObject root, final ICamera cam) {
		super(root, cam);
	}

	@Override
	public void renderObject(final SceneObject object, final Matrix4f parentTransform) {
		if (isVisible(object, parentTransform)) {
			super.renderObject(object, parentTransform);
		}
	}

	/**
	 * check for visibility of an object
	 *
	 * @param object
	 *            the object to check
	 * @param parentTransform
	 *            the current transformation
	 * @return false if this object would not be visible if rendered
	 */
	private boolean isVisible(final SceneObject object, final Matrix4f parentTransform) {
		final Vector3f[] corners = object.getBoundingBox().getCornersOnScreen(parentTransform, cam);
		float minX = corners[0].x;
		float minY = corners[0].y;
		float maxX = corners[0].x;
		float maxY = corners[0].y;

		float maxZ = corners[0].z;

		for (final Vector3f e : corners) {
			minX = java.lang.Math.min(minX, e.x);
			maxX = java.lang.Math.max(maxX, e.x);
			minY = java.lang.Math.min(minY, e.y);
			maxY = java.lang.Math.max(maxY, e.y);
			maxZ = java.lang.Math.max(maxZ, e.z);
		}
		final boolean intersect = minX < 1 && maxX > -1 && minY < 1 && maxY > -1;
		if (!intersect || maxZ < 0) {
			return false;
		}
		//otherwise check if big enough
		final float area = (maxX - minX) * (maxY - minY);

		return area >= MIN_SCREEN_AREA;
	}

}
