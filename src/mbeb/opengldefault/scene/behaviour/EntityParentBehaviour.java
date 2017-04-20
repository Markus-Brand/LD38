package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.IEntity;

/**
 * place an entity relative to another entity
 */
public class EntityParentBehaviour implements IBehaviour {

	/** the parent SceneObject the bone belongs to */
	private IEntity parentObject;

	public EntityParentBehaviour(IEntity parentObject) {
		this.parentObject = parentObject;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		entity.setPosition(parentObject.getPosition());
		entity.setDirection(parentObject.getDirection());
	}
}
