package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.entities.IEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * place an entity relative to another entity
 */
public class ParentBehaviour implements IBehaviour {

	/** the parent SceneObject the bone belongs to */
	private SceneObject parentObject;
	/** a position in local bone space (0^3 to be exactly at the bones origin) */
	private Vector3f localPosition;

	private Vector3f localDirection;

	public ParentBehaviour(SceneObject parent) {
		this(parent, new Vector3f());
	}

	public ParentBehaviour(SceneObject parentObject, Vector3f localPosition) {
		this(parentObject, localPosition, new Vector3f(0, 1, 0));
	}

	public ParentBehaviour(SceneObject parentObject, Vector3f localPosition, Vector3f localDirection) {
		this.parentObject = parentObject;
		this.localPosition = localPosition;
		this.localDirection = localDirection;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		Matrix4f parentTrans = parentObject.getGLobalTransformation().asMatrix();

		Vector4f globalPosition = parentTrans.transform(new Vector4f(localPosition, 1));
		entity.setPosition(new Vector3f(globalPosition.x, globalPosition.y, globalPosition.z));

		Vector4f globalDirection = parentTrans.transform(new Vector4f(localDirection, 1)).sub(globalPosition, new Vector4f());
		Vector3f globalDirection3 = new Vector3f(globalDirection.x, globalDirection.y, globalDirection.z).normalize();
		entity.setDirection(globalDirection3);
	}

	@Override
	public boolean triggers(IEntity entity) {
		return true;
	}
}
