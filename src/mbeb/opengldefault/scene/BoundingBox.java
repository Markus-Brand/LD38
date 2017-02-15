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
			this(Transformation.identity());
		}

		public Empty(final Transformation localToWorld) {
			super(null, null, localToWorld);
		}

		@Override
		public BoundingBox duplicate() {
			return new Empty(modelTransform);
		}

		@Override
		public BoundingBox unionWith(final BoundingBox other) {
			final BoundingBox result = other.duplicate();
			result.setModelTransform(modelTransform.and(result.modelTransform));
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
	 * just the transformation for this object
	 */
	protected Transformation modelTransform;

	public BoundingBox(final Vector3f localStart, final Vector3f localSize) {
		this(localStart, localSize, Transformation.identity());
	}

	public BoundingBox(final Vector3f localStart, final Vector3f localSize, final Transformation localToWorld) {
		this.localStart = localStart;
		this.localSize = localSize;
		this.modelTransform = localToWorld;
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

	public Transformation getModelTransform() {
		return modelTransform;
	}

	public void setModelTransform(final Transformation transform) {
		this.modelTransform = transform;
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
		final Vector3f[] childEdges = childBox.getGlobalEdges();
		BoundingBox bigger = this.duplicate();
		for (final Vector3f edge : childEdges) {
			bigger = bigger.extendTo(edge);
		}
		return bigger;
	}

	/**
	 * collect the 8 edges in local space
	 *
	 * @return
	 */
	private Vector3f[] getLocalEdges() {
		final Vector3f[] rawEdges = new Vector3f[8];
		for (int x = 0; x <= 1; x++) {
			for (int y = 0; y <= 1; y++) {
				for (int z = 0; z <= 1; z++) {
					final Vector3f res = new Vector3f(localSize);
					res.mul(x, y, z);
					res.add(localStart);
					rawEdges[x * 4 + y * 2 + z] = res;
				}
			}
		}
		return rawEdges;
	}

	/**
	 * apply own transformation to local edges and return them
	 *
	 * @return
	 */
	private Vector3f[] getGlobalEdges() {
		final Vector3f[] edges = getLocalEdges();
		for (int e = 0; e < edges.length; e++) {
			edges[e] = getModelTransform().applyTo3(edges[e]);
		}
		return edges;
	}

	/**
	 * calculate the screen-space positions of my 8 edges
	 *
	 * @param parentTransform
	 *            the boundingBoxes parent transformation
	 * @param camera
	 *            the camera to look from
	 * @return screen-space positions
	 */
	public Vector3f[] getEdgesOnScreen(final Transformation parentTransform, final ICamera camera) {
		//apply to edges
		final Vector3f[] edges = getGlobalEdges();
		for (int e = 0; e < edges.length; e++) {
			edges[e] = camera.getPosOnScreen(parentTransform.applyTo3(edges[e]));
		}
		return edges;
	}
}
