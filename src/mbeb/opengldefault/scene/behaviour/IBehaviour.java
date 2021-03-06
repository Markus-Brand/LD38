package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.*;

/**
 * Abstract Behaviour.
 * Each {@link Entity} has a list of Behaviours that describe what it does each update
 * 
 * @author Markus
 */
public interface IBehaviour {

	/**
	 * Does the Behaviour currently apply?
	 * 
	 * @param entity
	 *            the Entity, that is affected by this behaviour
	 * @return
	 */
	default boolean triggers(IEntity entity) {
		return true;
	}

	/**
	 * Updates the entity with this behaviour
	 * 
	 * @param deltaTime
	 *            time that passed since the last update
	 * @param entity
	 *            the Entity, that is affected by this behaviour
	 */
	void update(double deltaTime, IEntity entity);

	/**
	 * @return a new identical behaviour, which doesn't alter the rotation of the entity
	 */
	default IBehaviour fixedDirection() {
		return new RestrictedBehaviour(this, true, false);
	}

	/**
	 * @return a new identical behaviour, which doesn't alter the location of the entity
	 */
	default IBehaviour fixedLocation() {
		return new RestrictedBehaviour(this, false, true);
	}
}
