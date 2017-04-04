package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.RestrictedEntity;

/**
 * A Behaviour wrapper restricting the change of some properties of the target entity
 */
public class RestrictedBehaviour implements IBehaviour {

	private IBehaviour wrapped;

	private boolean move;
	private boolean rotate;

	public RestrictedBehaviour(IBehaviour wrapped, boolean move, boolean rotate) {
		this.wrapped = wrapped;
		this.move = move;
		this.rotate = rotate;
	}

	@Override
	public final void update(double deltaTime, IEntity entity) {
		wrapped.update(deltaTime, new RestrictedEntity(entity, move, rotate));
	}

	@Override
	public boolean triggers(IEntity entity) {
		return wrapped.triggers(entity);
	}
}
