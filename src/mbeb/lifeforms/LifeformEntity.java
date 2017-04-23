package mbeb.lifeforms;

import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.entities.*;

public abstract class LifeformEntity extends SceneEntity {

	float healthpoints;

	public LifeformEntity(final SceneObject sceneObject, final float healthpoints) {
		super(sceneObject);
		this.healthpoints = healthpoints;

	}
}
