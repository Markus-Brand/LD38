package mbeb.lifeforms;

import org.joml.*;

import mbeb.opengldefault.scene.*;

public class Player extends Lifeform {
	PlayerEntity playerEntity;

	public Player(final float healthpoints) {
		super(healthpoints);
	}

	@Override
	protected PlayerEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent) {
		return null;
	}

}
