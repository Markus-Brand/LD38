package mbeb.opengldefault.scene.behaviour;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import mbeb.opengldefault.scene.entities.IEntity;

public class TopDownViewBehaviour extends ReferenceEntityBehaviour {

	private float angle;
	private float distance;

	public TopDownViewBehaviour(IEntity reference) {
		super(reference);
		angle = (float) Math.PI / 3f;
		distance = 10f;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		Vector3f relativePosition = new Vector3f(getReference().getDirection());
		relativePosition = new Vector3f(relativePosition.x, 0, relativePosition.z).normalize();
		Vector3f axis = new Vector3f(0, 1, 0).cross(relativePosition);
		relativePosition.rotate(new Quaternionf(new AxisAngle4f(angle, axis)));

		entity.setPosition(relativePosition.mul(-distance, new Vector3f()).add(getReference().getPosition()));
		entity.setDirection(relativePosition);
	}

}
