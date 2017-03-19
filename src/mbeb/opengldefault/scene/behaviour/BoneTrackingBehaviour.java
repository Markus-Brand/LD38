package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.animation.AnimatedRenderable;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.entities.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * place an entity relative to a bone
 */
public class BoneTrackingBehaviour implements IBehaviour {

	/** the id of the bone to track to */
	private int boneID;
	/** the parent SceneObject the bone belongs to */
	private SceneObject animatedObject;
	/** the pose-state-containing object */
	private AnimatedRenderable renderable;
	/** a position in local bone space (0^3 to be exactly at the bones origin) */
	private Vector3f localPosition;

	public BoneTrackingBehaviour(SceneObject animatedObject, AnimatedRenderable renderable, String boneName) {
		this(animatedObject, renderable, boneName, new Vector3f());
	}

	public BoneTrackingBehaviour(SceneObject animatedObject, AnimatedRenderable renderable, String boneName, Vector3f localPosition) {
		this.boneID = renderable.getAnimatedMesh().getSkeleton().firstBoneNamed(boneName).getIndex();
		this.animatedObject = animatedObject;
		this.renderable = renderable;
		this.localPosition = localPosition;
	}

	@Override
	public void update(double deltaTime, Entity entity) {
		Matrix4f boneTrans = renderable.getCurrentPose().get(boneID).getPoseBoneTransform();
		Matrix4f parentTrans = animatedObject.getGLobalTransformation().asMatrix();

		Matrix4f finalTrans = parentTrans.mul(boneTrans, new Matrix4f());

		Vector4f target4 = finalTrans.transform(new Vector4f(localPosition, 1));
		Vector3f target3 = new Vector3f(target4.x, target4.y, target4.z);

		entity.setPosition(target3);
	}

	@Override
	public boolean triggers(Entity entity) {
		return true;
	}
}
