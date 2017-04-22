package mbeb.opengldefault.scene.behaviour;

import org.joml.Vector3f;

import mbeb.opengldefault.scene.entities.IEntity;

public class LookAtBehaviour extends ReferenceEntityBehaviour {

	public LookAtBehaviour(IEntity reference) {
		super(reference);
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		Vector3f direction = getDirectionTo(entity);
		entity.setDirection(direction);
	}

	protected Vector3f getDirectionTo(IEntity entity) {
		return getReference().getPosition().sub(entity.getPosition(), new Vector3f());
	}
}
