package mbeb.opengldefault.scene.entities;

import mbeb.opengldefault.scene.behaviour.IBehaviour;
import org.joml.Vector3f;

import java.util.Set;

/**
 * A Decorator for IEntity to not pass rotation or translation commands
 */
public class RestrictedEntity implements IEntity {

	private final IEntity wrappedObject;
	private final boolean move, rotate;

	public RestrictedEntity(IEntity wrappedObject, boolean move, boolean rotate) {
		this.wrappedObject = wrappedObject;
		this.move = move;
		this.rotate = rotate;
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
		if (move) {
			wrappedObject.setPosition(position);
		}
	}

	@Override
	public void setDirection(Vector3f direction) {
		if (rotate) {
			wrappedObject.setDirection(direction);
		}
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
	public IEntity addBehaviour(int priority, IBehaviour behaviour) {
		wrappedObject.addBehaviour(priority, behaviour);
		return this;
	}
}
