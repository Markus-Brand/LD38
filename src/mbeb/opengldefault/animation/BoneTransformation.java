package mbeb.opengldefault.animation;

import org.joml.*;
import org.lwjgl.assimp.*;

/**
 * a Transformation with convenient functions.
 */
public class BoneTransformation {

	public static BoneTransformation identity() {
		return new BoneTransformation(null, null, null);
	}

	public static final Matrix4f matrixFromAI(AIMatrix4x4 aimat) {
		Matrix4f mat = new Matrix4f(aimat.a1(), aimat.b1(), aimat.c1(), aimat.d1(), aimat.a2(), aimat.b2(), aimat.c2(), aimat.d2(), aimat.a3(), aimat.b3(), aimat.c3(), aimat.d3(), aimat.a4(),
				aimat.b4(), aimat.c4(), aimat.d4());
		return mat;
	}

	private static Vector3f lerpVec3(Vector3f a, Vector3f b, double factor) {
		return a.lerp(b, (float) factor, new Vector3f());
	}

	private static Quaternionf lerpQuaternion(Quaternionf a, Quaternionf b, double factor) {
		return a.slerp(b, (float) factor, new Quaternionf());
	}

	/**
	 * lerp between two transformations
	 *
	 * @param t1
	 * @param t2
	 * @param factor
	 * @return
	 */
	public static BoneTransformation lerp(BoneTransformation t1, BoneTransformation t2, double factor) {
		if (factor == 0) {
			return t1;
		}
		if (factor == 1) {
			return t2;
		}
		return new BoneTransformation(
				lerpVec3(t1.getPosition(), t2.getPosition(), factor),
				lerpQuaternion(t1.getRotation(), t2.getRotation(), factor),
				lerpVec3(t1.getScale(), t2.getScale(), factor));

	}

	//////////////////////////////////////////////////////////////////////////////////////////
	private Vector3f position;
	private Quaternionf rotation;
	private Vector3f scale;

	public BoneTransformation(Vector3f position) {
		this(position, null);
	}

	public BoneTransformation(Vector3f position, Quaternionf rotation) {
		this(position, rotation, null);
	}

	public BoneTransformation(Vector3f position, Quaternionf rotation, Vector3f scale) {
		this.position = position != null ? position : new Vector3f(0);
		this.rotation = rotation != null ? rotation : new Quaternionf();
		this.scale = scale != null ? scale : new Vector3f(1);
	}

	public BoneTransformation(Matrix4f mat) {
		this(mat.getTranslation(new Vector3f()), mat.getNormalizedRotation(new Quaternionf()), mat.getScale(new Vector3f()));

	}

	/**
	 * combine two transformations
	 *
	 * @param other
	 *            the other transformation to perform "after" this one
	 * @return a new combined transformation
	 */
	public BoneTransformation and(BoneTransformation other) {
		//todo check if this method is used the right way always
		Matrix4f mul = new Matrix4f();
		this.asMatrix().mul(other.asMatrix(), mul);
		return new BoneTransformation(mul);
	}

	/**
	 * @return a matrix representing this transformation
	 */
	public Matrix4f asMatrix() {
		return new Matrix4f().translate(getPosition()).scale(getScale()).rotate(getRotation().normalize());
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
		return "Transform( loc=" + getPosition() + ", rot=" + getRotation() + ", scale=" + getScale() + ")";
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setPosition(Vector4f position) {
		setPosition(new Vector3f(position.x / position.w, position.y / position.w, position.z / position.w));
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public void setRotation(Quaternionf rotation) {
		this.rotation = rotation;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
}
