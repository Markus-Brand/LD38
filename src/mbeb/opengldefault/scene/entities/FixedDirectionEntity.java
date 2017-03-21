package mbeb.opengldefault.scene.entities;

import mbeb.opengldefault.scene.behaviour.IBehaviour;
import org.joml.Vector3f;

import java.util.Set;

/**
 * A Decorator for IEntity not passing any SetDirection-calls
 */
public class FixedDirectionEntity implements IEntity {

	private final IEntity wrappedObject;

	public FixedDirectionEntity(IEntity wrappedObject) {
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
		wrappedObject.setPosition(position);
	}

	@Override
	public void setDirection(Vector3f direction) {
		//nothing
	}

	@Override
	public void update(double deltaTime) {
		wrappedObject.update(deltaTime);
	}

	@Override
	public Set<PrioritizedBehaviour> getBehaviours() {
		return wrappedObject.getBehaviours();
	}

	@Override
	public IEntity addBehaviour(int priority, IBehaviour behaviour) {
		wrappedObject.addBehaviour(priority, behaviour);
		return this;
	}
}
