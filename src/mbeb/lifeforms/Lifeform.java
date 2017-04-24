package mbeb.lifeforms;

import mbeb.ld38.HealthBarGUI;
import org.joml.*;

import mbeb.opengldefault.scene.*;

public abstract class Lifeform {
	protected float radius;
	protected float healthpoints;

	protected Lifeform(final float radius, final float healthpoints) {
		this.radius = radius;
		setHealthpoints(healthpoints);
	}

	public float getHealthpoints() {
		return healthpoints;
	}

	public void setHealthpoints(final float healthpoints) {
		this.healthpoints = healthpoints;
	}

	protected abstract LifeformEntity spawnNew(final Vector3f position, final float angle, final SceneObject parent, final HealthBarGUI healthBarGui);
}
