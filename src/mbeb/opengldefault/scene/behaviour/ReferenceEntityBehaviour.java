package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.*;

/**
 * A abstract Behaviour that interacts with a reference Entity
 * 
 * @author Markus
 */
public abstract class ReferenceEntityBehaviour implements IBehaviour {

	private final IEntity reference;

	public ReferenceEntityBehaviour(IEntity reference) {
		this.reference = reference;
	}

	public IEntity getReference() {
		return reference;
	}

	public LimitedDistanceBehaviour limited(float distance) {
		return new LimitedDistanceBehaviour(this, distance);
	}
}
