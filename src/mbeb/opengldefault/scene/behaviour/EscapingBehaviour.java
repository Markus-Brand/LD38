package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.*;

/**
 * A Behaviour that makes a Entity run away from another Entity
 * 
 * @author Markus
 */
public class EscapingBehaviour extends FollowingBehaviour {

	public EscapingBehaviour(Entity followed, float speed) {
		super(followed, -speed);
	}

}
