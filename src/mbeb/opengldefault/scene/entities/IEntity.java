package mbeb.opengldefault.scene.entities;

import mbeb.opengldefault.scene.behaviour.IBehaviour;
import org.joml.Vector3f;

import java.util.Set;

/**
 * A thing inside the world that can obtain behaviours
 */
public interface IEntity {

	/**
	 * Contains a Behaviour and a priority. The class is used to sort the Behaviours
	 * Note: this class has a natural ordering that is inconsistent with equals.
	 *
	 * @author Markus
	 */
	class PrioritizedBehaviour implements Comparable<Entity.PrioritizedBehaviour> {

		public IBehaviour behaviour;
		public int priority;

		public PrioritizedBehaviour(IBehaviour behaviour, int priority) {
			this.behaviour = behaviour;
			this.priority = priority;
		}

		@Override
		public int compareTo(Entity.PrioritizedBehaviour o) {
			return Integer.compare(priority, o.priority);
		}

	}

	/**
	 * @return the Entities position
	 */
	Vector3f getPosition();

	/**
	 * @return the Entities direction
	 */
	Vector3f getDirection();

	/**
	 * @param position
	 *            the new position for the Entity
	 */
	void setPosition(Vector3f position);

	/**
	 * @param direction
	 *            the new direction for the Entity
	 */
	void setDirection(Vector3f direction);

	/**
	 * Updates the Entity by updating all of the Behaviours
	 *
	 * @param deltaTime
	 */
	void update(double deltaTime);

	/**
	 * @return the current Behaviours and their
	 */
	Set<Entity.PrioritizedBehaviour> getBehaviours();

	/**
	 * Adds a behaviour with given priority.
	 *
	 * @param priority
	 *            low value is higher priority
	 * @param behaviour
	 *            the new Behaviour
	 */
	void addBehaviour(int priority, IBehaviour behaviour);


}
