package mbeb.opengldefault.scene.entities;

import java.util.*;

import org.joml.*;

import mbeb.opengldefault.scene.behaviour.*;

/**
 * An abstract Entity that can have Behaviours
 *
 * @author Markus
 */
public abstract class Entity implements IEntity {

	protected Set<PrioritizedBehaviour> behaviours;

	public Entity() {
		behaviours = new TreeSet<>();
	}

	/**
	 * @return the Entities position
	 */
	public abstract Vector3f getPosition();

	/**
	 * @return the Entities direction
	 */
	public abstract Vector3f getDirection();

	/**
	 * @param position
	 *            the new position for the Entity
	 */
	public abstract void setPosition(Vector3f position);

	/**
	 * @param direction
	 *            the new direction for the Entity
	 */
	public abstract void setDirection(Vector3f direction);

	/**
	 * Updates the Entity by updating all of the Behaviours
	 *
	 * @param deltaTime
	 */
	public void update(double deltaTime) {
		for (PrioritizedBehaviour prioBehaviour : behaviours) {
			IBehaviour behaviour = prioBehaviour.getBehaviour();
			if (behaviour.triggers(this)) {
				behaviour.update(deltaTime, this);
				break;
			}
		}
	}

	/**
	 * @return the current Behaviours and their
	 */
	public Set<PrioritizedBehaviour> getBehaviours() {
		return behaviours;
	}

	/**
	 * Adds a behaviour with given priority.
	 *
	 * @param priority
	 *            low value is higher priority
	 * @param behaviour
	 *            the new Behaviour
	 */
	public IEntity addBehaviour(int priority, IBehaviour behaviour) {
		behaviours.add(new PrioritizedBehaviour(behaviour, priority));
		return this;
	}
}
