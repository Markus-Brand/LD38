package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.*;

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
}
