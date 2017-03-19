package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.*;

/**
 * A Behaviour that only applies if a Reference Entity is near enough
 * 
 * @author Markus
 */
public class LimitedDistanceBehaviour implements IBehaviour {

	/** minimum distance that is needed for the Behaviour to apply */
	private float distance;
	/** the behaviour that will be used if the reference Entity is near enough */
	private ReferenceEntityBehaviour behaviour;

	public LimitedDistanceBehaviour(ReferenceEntityBehaviour behaviour, float distance) {
		this.behaviour = behaviour;
		this.distance = distance;
	}

	@Override
	public void update(double deltaTime, Entity entity) {
		behaviour.update(deltaTime, entity);
	}

	@Override
	public boolean triggers(Entity entity) {
		return behaviour.triggers(entity) && entity.getPosition().distance(behaviour.getReference().getPosition()) < distance;
	}
}
