package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.Entity;

/**
 * A abstract Behaviour that interacts with a reference Entity
 * 
 * @author Markus
 */
public abstract class ReferenceEntityBehaviour implements IBehaviour {

	private final Entity reference;

	public ReferenceEntityBehaviour(Entity reference) {
		this.reference = reference;
	}

	public Entity getReference() {
		return reference;
	}
	
	public LimitedDistanceBehaviour limited(float distance) {
		return new LimitedDistanceBehaviour(this, distance);
	}
}
