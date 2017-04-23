package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.IEntity;
import org.joml.Vector3f;

/**
 * Top down view behaviour with fixed up vector.
 */
public class DungeounViewBehaviour extends ReferenceEntityBehaviour {

	private float height;
	private Vector3f up;

	public DungeounViewBehaviour(IEntity reference, float height) {
		this(reference, height, new Vector3f(0,0,1));
	}

	public DungeounViewBehaviour(IEntity reference, float height, Vector3f vector3f) {
		super(reference);
		
	}

	@Override
	public void update(double deltaTime, IEntity entity) {

	}
}
