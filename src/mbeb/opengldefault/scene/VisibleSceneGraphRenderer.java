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

	@Deprecated
	private static final float MIN_SCREEN_AREA = 1f;

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
		Iterator<Vector3f> cornerIterator = object.getBoundingBox().getCornersOnScreen(parentTransform, camera);

		List<Vector3f> corners = new ArrayList<>();
		while (cornerIterator.hasNext()) {
			Vector3f corner = cornerIterator.next();
			if (corner.x > -1 && corner.x < 1 && corner.y > -1 && corner.y < 1 && corner.z > -1 && corner.z < 1) {
				return true;
			}/**/
			corners.add(corner);
		}

		float maxZ = corners.get(0).z;
		float minZ = corners.get(0).z;

		for (final Vector3f e : corners) {
			maxZ = java.lang.Math.max(maxZ, e.z);
			minZ = java.lang.Math.min(minZ, e.z);
		}
		//todo: could probably not work with giant objects
		if ((maxZ > 1 || maxZ < 0) && (minZ > 1 || minZ < -1)) {
			return false;
		}

		float minX = corners.get(0).x;
		float minY = corners.get(0).y;
		float maxX = corners.get(0).x;
		float maxY = corners.get(0).y;
		for (final Vector3f e: corners) {
			minX = java.lang.Math.min(minX, e.x);
			maxX = java.lang.Math.max(maxX, e.x);
			minY = java.lang.Math.min(minY, e.y);
			maxY = java.lang.Math.max(maxY, e.y);
		}

		final boolean intersect = minX < 1 && maxX > -1 && minY < 1 && maxY > -1;
		if (!intersect) {
			return false;
		}
		return true;
		//otherwise check if big enough
		//final float area = (maxX - minX) * (maxY - minY);
		//System.out.println(maxX);
		//System.out.println(area);

		//return area >= MIN_SCREEN_AREA;
	}

}
