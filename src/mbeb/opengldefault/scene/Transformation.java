package mbeb.opengldefault.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * a Transformation with convenient functions
 */
public class Transformation {

	public static Transformation identity() {
		return new Transformation(new Matrix4f());
	}

	/**
	 * create a new Transformation that translates
	 *
	 * @param pos
	 *            translation
	 * @return
	 */
	public static Transformation fromPosition(Vector3f pos) {
		Transformation transform = identity();
		transform.matrix.translate(pos);
		return transform;
	}

	private final Matrix4f matrix;

	public Transformation(Matrix4f matrix) {
		this.matrix = matrix;
	}

	/**
	 * combine two transformations
	 *
	 * @param other
	 *            the other transformation to perform "after" this one
	 * @return a new combined transformation
	 */
	public Transformation and(Transformation other) {
		//todo check if this method is used the right way alsways
		Matrix4f mul = new Matrix4f();
		this.asMatrix().mul(other.asMatrix(), mul);
		return new Transformation(mul);
	}

	/**
	 * @return a matrix representing this transformation
	 */
	public Matrix4f asMatrix() {
		return matrix;
	}

	/**
	 * apply this transformation to a vector
	 *
	 * @param vec
	 * @return a vec3 instead, components divided by w
	 */
	public Vector3f applyTo3(Vector3f vec) {
		Vector4f applied = applyTo(vec);
		return new Vector3f(applied.x / applied.w, applied.y / applied.w, applied.z / applied.w);
	}

	/**
	 * apply this transformation to a vector
	 *
	 * @param vec
	 * @return
	 */
	public Vector4f applyTo(Vector3f vec) {
		return applyTo(new Vector4f(vec.x, vec.y, vec.z, 1));
	}

	/**
	 * apply this transformation to a vector
	 *
	 * @param vec
	 * @return
	 */
	public Vector4f applyTo(Vector4f vec) {
		Vector4f res = new Vector4f();
		vec.mul(asMatrix(), res);
		return res;
	}

	@Override
	public String toString() {
		return asMatrix().toString();
	}

}
