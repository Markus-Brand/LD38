package mbeb.opengldefault.scene;

import mbeb.opengldefault.camera.*;

import org.joml.*;

/**
 * A simple struct defining a box-like area in the world
 */
public class BoundingBox {

	/**
	 * A boundingBox Null-object, indicating that it is empty
	 */
	public static class Empty extends BoundingBox {

		public Empty() {
			this(new Matrix4f());
		}

		public Empty(final Matrix4f localToWorld) {
			super(null, null, localToWorld);
		}

		@Override
		public BoundingBox duplicate() {
			return new Empty(modelTransform);
		}

		@Override
		public BoundingBox unionWith(final BoundingBox other) {
			BoundingBox result = other.duplicate();
			result.setModelTransform(modelTransform.mul(result.modelTransform, new Matrix4f()));
			return result;
		}

		@Override
		public BoundingBox extendTo(final Vector3f localVertex) {
			return new BoundingBox(localVertex, new Vector3f(0), modelTransform);
		}

	};

	/**
	 * local 3d aabb
	 */
	protected Vector3f localStart;
	protected Vector3f localSize;

	/**
	 * global 3d aabb
	 */
	protected Vector3f globalStart;
	protected Vector3f globalEnd;
	private Vector3f color;

	/**
	 * the transformation for this object
	 */
	protected Matrix4f modelTransform;

	public BoundingBox(final Vector3f localStart, final Vector3f localSize) {
		this(localStart, localSize, new Matrix4f());
	}

	public BoundingBox(final Vector3f localStart, final Vector3f localSize, final Matrix4f localToWorld) {
		this.localStart = localStart;
		this.localSize = localSize;
		this.modelTransform = localToWorld;
		color = new Vector3f(1, 0, 0);
	}

	/**
	 * @return a new equivalent BoundingBox
	 */
	public BoundingBox duplicate() {
		return new BoundingBox(new Vector3f(localStart), new Vector3f(localSize), modelTransform);
	}

	/**
	 * return a bigger box that also contains provided vertex
	 *
	 * @param localVertex
	 *            a new vertex to include in this box
	 * @return a possibly new BoundingBox-object
	 */
	public BoundingBox extendTo(final Vector3f localVertex) {
		final Vector3f localEnd = new Vector3f(localStart).add(localSize);

		localStart.x = java.lang.Math.min(localStart.x, localVertex.x);
		localStart.y = java.lang.Math.min(localStart.y, localVertex.y);
		localStart.z = java.lang.Math.min(localStart.z, localVertex.z);

		localEnd.x = java.lang.Math.max(localEnd.x, localVertex.x);
		localEnd.y = java.lang.Math.max(localEnd.y, localVertex.y);
		localEnd.z = java.lang.Math.max(localEnd.z, localVertex.z);

		localEnd.sub(localStart, localSize);

		return this;
	}

	/**
	 * Getter for the model transform
	 *
	 * @return the model transform
	 */
	public Matrix4f getModelTransform() {
		return modelTransform;
	}

	/**
	 * Setter for the model Transform
	 *
	 * @param transform
	 *            new model transform
	 */
	public void setModelTransform(final Matrix4f transform) {
		this.modelTransform = transform;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	@Override
	public String toString() {
		localSize.toString();
		return "BoundingBox(start=" + localStart + ", size=" + localSize + ", transform=" + modelTransform + ")";
	}

	/**
	 * create a new boundingBox with the child-box and this one combined
	 *
	 * @param childBox
	 *            - its translation is seen as relative to this ones
	 * @return a new boundingBox
	 */
	public BoundingBox unionWith(final BoundingBox childBox) {
		//We also need to check for updates in the child BB, because the BB could potentially grow, if the children have transformations on their own
		final Vector3f[] childEdges = childBox.getGlobalCorners();
		BoundingBox bigger = this.duplicate();
		for (final Vector3f corner : childEdges) {
			bigger = bigger.extendTo(corner);
		}
		return bigger;
	}

	/**
	 * collect the 8 corners in local space
	 *
	 * @return
	 */
	public Vector3f[] getLocalCorners() {
		final Vector3f[] rawCorners = new Vector3f[8];
		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				for (int z = 0; z <= 1; z++) {
					final Vector3f res = new Vector3f(localSize);
					res.mul(x, y, z);
					res.add(localStart);
					rawCorners[x * 4 + y * 2 + z] = res;
				}
			}
		}
		return rawCorners;
	}

	/**
	 * apply own transformation to local corners and return them
	 *
	 * @return
	 */
	public Vector3f[] getGlobalCorners() {
		final Vector3f[] corners = getLocalCorners();
		for (int e = 0; e < corners.length; e++) {
			Vector4f corner = new Vector4f(corners[e], 1.0f).mul(getModelTransform());
			corners[e] = new Vector3f(corner.x, corner.y, corner.z);
		}
		return corners;
	}

	/**
	 * calculate the screen-space positions of my 8 corners
	 *
	 * @param parentTransform
	 *            the boundingBoxes parent transformation
	 * @param camera
	 *            the camera to look from
	 * @return screen-space positions
	 */
	public Vector3f[] getCornersOnScreen(final Matrix4f parentTransform, final ICamera camera) {
		final Vector3f[] corners = getGlobalCorners();
		float minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;
		for (int e = 0; e < corners.length; e++) {
			Vector4f worldCorner = new Vector4f(corners[e], 1.0f).mul(parentTransform);
			if (e == 0) {
				minX = worldCorner.x;
				minY = worldCorner.y;
				minZ = worldCorner.z;
				maxX = worldCorner.x;
				maxY = worldCorner.y;
				maxZ = worldCorner.z;
			} else {
				minX = java.lang.Math.min(worldCorner.x, minX);
				minY = java.lang.Math.min(worldCorner.y, minY);
				minZ = java.lang.Math.min(worldCorner.z, minZ);
				maxX = java.lang.Math.max(worldCorner.x, maxX);
				maxY = java.lang.Math.max(worldCorner.y, maxY);
				maxZ = java.lang.Math.max(worldCorner.z, maxZ);
			}
			corners[e] = camera.getPosOnScreen(worldCorner);
		}
		globalStart = new Vector3f(minX, minY, minZ);
		globalEnd = new Vector3f(maxX, maxY, maxZ);
		return corners;
	}

	/**
	 * Returns the center of the BoundingBox
	 *
	 * @return center of the BoundingBox
	 */
	public Vector3f getCenter() {
		Vector3f halfSize = localSize.mul(0.5f, new Vector3f());
		return localStart.add(halfSize, new Vector3f());
	}

	public boolean intersectsRay(Vector3f origin, Vector3f direction)
	{
		Vector3f min = globalStart;
		Vector3f max = globalEnd;
		if (min == null || max == null) {
			return false;
		}
		float tmin = (min.x - origin.x) / direction.x;
		float tmax = (max.x - origin.x) / direction.x;

		if (tmin > tmax) {
			float buffer = tmin;
			tmin = tmax;
			tmax = buffer;
		}

		float tymin = (min.y - origin.y) / direction.y;
		float tymax = (max.y - origin.y) / direction.y;

		if (tymin > tymax) {
			float buffer = tymin;
			tymin = tymax;
			tymax = buffer;
		}

		if (tmin > tymax || tymin > tmax) {
			return false;
		}

		if (tymin > tmin) {
			tmin = tymin;
		}

		if (tymax < tmax) {
			tmax = tymax;
		}

		float tzmin = (min.z - origin.z) / direction.z;
		float tzmax = (max.z - origin.z) / direction.z;

		if (tzmin > tzmax) {
			float buffer = tzmin;
			tzmin = tzmax;
			tzmax = buffer;
		}

		if (tmin > tzmax || tzmin > tmax) {
			return false;
		}

		if (tzmin > tmin) {
			tmin = tzmin;
		}

		if (tzmax < tmax) {
			tmax = tzmax;
		}

		return true;
	}
}
