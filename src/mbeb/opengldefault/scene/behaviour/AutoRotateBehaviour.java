package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.IEntity;
import org.joml.Vector3f;

/**
 * This behaviour automatically rotates an object based on their movement path
 */
public class AutoRotateBehaviour implements IBehaviour {
	
	private Vector3f lastPosition = null;
	
	@Override
	public boolean triggers(IEntity entity) {
		return true;
	}
	
	@Override
	public void update(double deltaTime, IEntity entity) {
		if (lastPosition == null) {
			lastPosition = new Vector3f(entity.getPosition());
			return;
		}
		Vector3f currentPosition = entity.getPosition();
		Vector3f direction = currentPosition.sub(lastPosition, new Vector3f());
		if (Math.abs(direction.lengthSquared()) < 0.0001f) {
			return;
		}
		direction.normalize();
		entity.setDirection(direction);
		lastPosition = new Vector3f(currentPosition);
	}
}
