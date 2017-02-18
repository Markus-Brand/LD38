package mbeb.opengldefault.scene.entities;

import java.util.Set;
import java.util.TreeSet;

import mbeb.opengldefault.scene.behaviour.Behaviour;

import org.joml.Vector3f;

public abstract class Entity {

	private class PrioritizedBehaviour implements Comparable<PrioritizedBehaviour> {

		public Behaviour behaviour;
		public int priority;

		public PrioritizedBehaviour(Behaviour behaviour, int priority) {
			this.behaviour = behaviour;
			this.priority = priority;
		}

		@Override
		public int compareTo(PrioritizedBehaviour o) {
			return new Integer(priority).compareTo(o.priority);
		}

	}

	protected Set<PrioritizedBehaviour> behaviours;

	public Entity() {
		behaviours = new TreeSet<>();
	}

	public abstract Vector3f getPosition();

	public abstract Vector3f getDirection();

	public abstract void setPosition(Vector3f position);

	public abstract void setDirection(Vector3f direction);

	public void update(double deltaTime) {
		for (PrioritizedBehaviour prioBehaviour : behaviours) {
			Behaviour behaviour = prioBehaviour.behaviour;
			if (behaviour.triggers(this)) {
				behaviour.update(deltaTime, this);
				break;
			}
		}
	}

	public Set<PrioritizedBehaviour> getBehaviours() {
		return behaviours;
	}

	public void addBehaviour(int priority, Behaviour behaviour) {
		behaviours.add(new PrioritizedBehaviour(behaviour, priority));
	}
}
