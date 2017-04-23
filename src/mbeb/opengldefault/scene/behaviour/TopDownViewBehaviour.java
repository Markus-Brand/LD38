package mbeb.opengldefault.scene.behaviour;

import org.joml.Vector2f;
import org.joml.Vector3f;

import mbeb.opengldefault.scene.entities.IEntity;

public class TopDownViewBehaviour extends ReferenceEntityBehaviour {

	private float height;
	private float thresholdRadius;
	private float speed;

	public TopDownViewBehaviour(IEntity reference, float height, float thresholdRadius, float speed) {
		super(reference);
		this.height = height;
		this.thresholdRadius = thresholdRadius;
		this.speed = speed;
	}

	@Override
	public void update(double deltaTime, IEntity topEntity) {
		IEntity reference = getReference();

		Vector3f relativePosition = reference.getPosition().sub(topEntity.getPosition(), new Vector3f());
		Vector2f planarMovement = new Vector2f(relativePosition.x, relativePosition.z);
		float dist = Math.max(planarMovement.length() - thresholdRadius, 0);
		planarMovement.normalize();
		planarMovement.mul(dist * speed * (float) deltaTime);

		Vector3f position = topEntity.getPosition().add(new Vector3f(planarMovement.x, 0, planarMovement.y));
		position.y = reference.getPosition().y + height;

		topEntity.setPosition(position);

		topEntity.setDirection(relativePosition.normalize());/**/

	}

}
