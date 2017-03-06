package mbeb.opengldefault.scene.entities;

import java.util.Set;
import java.util.TreeSet;

import mbeb.opengldefault.scene.behaviour.IBehaviour;

import org.joml.Vector3f;

/**
 * An abstract Entity that can have Behaviours
 *
 * @author Markus
 */
public abstract class Entity {

	/**
	 * Contains a Behaviour and a priority. The class is used to sort the Behaviours
	 * Note: this class has a natural ordering that is inconsistent with equals.
	 * @author Markus
	 */
	protected class PrioritizedBehaviour implements Comparable<PrioritizedBehaviour> {

		public IBehaviour behaviour;
		public int priority;

		public PrioritizedBehaviour(IBehaviour behaviour, int priority) {
			this.behaviour = behaviour;
			this.priority = priority;
		}

		@Override
		public int compareTo(PrioritizedBehaviour o) {
			return Integer.compare(priority, o.priority);
		}

	}

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
			IBehaviour behaviour = prioBehaviour.behaviour;
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
	public void addBehaviour(int priority, IBehaviour behaviour) {
		behaviours.add(new PrioritizedBehaviour(behaviour, priority));
	}
}
