package mbeb.opengldefault.scene;

import org.joml.*;

import mbeb.opengldefault.camera.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * only renders the visible part of the sceneGraph
 */
public class VisibleSceneGraphRenderer extends SceneGraphRenderer {

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
		if (object.getBoundingBox().isEmpty()) {
			return true;
		}
		
		Iterator<Vector3f> cornerIterator = object.getBoundingBox().getCornersOnScreen(parentTransform, camera);

		List<Vector3f> corners = new ArrayList<>();
		while (cornerIterator.hasNext()) {
			Vector3f corner = cornerIterator.next();
			if (corner.x > -1 && corner.x < 1 && corner.y > -1 && corner.y < 1 && corner.z > -1 && corner.z < 1) {
				return true;
			}
			corners.add(corner);
		}

		float maxZ = corners.get(0).z;
		float minZ = corners.get(0).z;

		for (final Vector3f c : corners) {
			maxZ = java.lang.Math.max(maxZ, c.z);
			minZ = java.lang.Math.min(minZ, c.z);
		}
		//todo: could probably not work with giant objects
		if (minZ > 1 || maxZ < -1) {
			return false;
		}

		float minX = corners.get(0).x;
		float minY = corners.get(0).y;
		float maxX = corners.get(0).x;
		float maxY = corners.get(0).y;
		for (final Vector3f c: corners) {
			minX = java.lang.Math.min(minX, c.x);
			maxX = java.lang.Math.max(maxX, c.x);
			minY = java.lang.Math.min(minY, c.y);
			maxY = java.lang.Math.max(maxY, c.y);
		}

		final boolean intersect = minX < 1 && maxX > -1 && minY < 1 && maxY > -1;
		
		return intersect;
	}

}
