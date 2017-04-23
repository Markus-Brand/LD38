package mbeb.lifeforms;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class JumpingBehaviour extends ReferenceEntityBehaviour {
	private static final String TAG = "JumpingBehaviour";

	public JumpingBehaviour(final PlayerEntity playerEntity) {
		super(playerEntity);
	}

	@Override
	public void update(final double deltaTime, final IEntity entity) {
		try {
			final MonsterEntity monsterEntity = (MonsterEntity) entity;
		} catch(final ClassCastException e) {
			Log.error(TAG, "Entity has to be a MonsterEntity to use " + TAG, e);
		}

		System.out.println("Jump!!!");
	}
}
