package mbeb.opengldefault.scene.behaviour;

import mbeb.opengldefault.scene.entities.FixedDirectionEntity;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.StationaryEntity;

/**
 * A Behaviour wrapper restricting the change of some properties of the target entity
 */
public class RestrictableBehaviour implements IBehaviour {

	private IBehaviour wrapped;

	private boolean move, rotate;

	public RestrictableBehaviour(IBehaviour wrapped, boolean move, boolean rotate) {
		this.wrapped = wrapped;
		this.move = move;
		this.rotate = rotate;
	}


	@Override
	public final void update(double deltaTime, IEntity entity) {
		IEntity wrapped = entity;
		if (!move) {
			wrapped = new StationaryEntity(wrapped);
		}
		if (!rotate) {
			wrapped = new FixedDirectionEntity(wrapped);
		}
		this.wrapped.update(deltaTime, wrapped);
	}

	@Override
	public boolean triggers(IEntity entity) {
		return wrapped.triggers(entity);
	}
}
