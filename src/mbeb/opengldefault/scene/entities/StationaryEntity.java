package mbeb.opengldefault.scene.entities;

import mbeb.opengldefault.scene.behaviour.IBehaviour;
import org.joml.Vector3f;

import java.util.Set;

/**
 * A Decorator for IEntity not passing any SetPosition-calls
 */
public class StationaryEntity implements IEntity {

	private final IEntity wrappedObject;

	public StationaryEntity(IEntity wrappedObject) {
		this.wrappedObject = wrappedObject;
	}

	@Override
	public Vector3f getPosition() {
		return wrappedObject.getPosition();
	}

	@Override
	public Vector3f getDirection() {
		return wrappedObject.getDirection();
	}

	@Override
	public void setPosition(Vector3f position) {
		//nothing
	}

	@Override
	public void setDirection(Vector3f direction) {
		wrappedObject.setDirection(direction);
	}

	@Override
	public void update(double deltaTime) {
		wrappedObject.update(deltaTime);
	}

	@Override
	public Set<Entity.PrioritizedBehaviour> getBehaviours() {
		return wrappedObject.getBehaviours();
	}

	@Override
	public void addBehaviour(int priority, IBehaviour behaviour) {
		wrappedObject.addBehaviour(priority, behaviour);
	}
}
