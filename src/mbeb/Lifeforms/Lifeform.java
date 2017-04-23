package mbeb.Lifeforms;

import org.joml.*;

import mbeb.opengldefault.scene.*;

public abstract class Lifeform {
	protected float healthpoints;

	public float getHealthpoints() {
		return healthpoints;
	}

	public void setHealthpoints(final float healthpoints) {
		this.healthpoints = healthpoints;
	}

	protected Lifeform(final float healthpoints) {
		setHealthpoints(healthpoints);
	}

	protected abstract LifeformEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent);
}
