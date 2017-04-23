package mbeb.lifeforms;

import org.joml.Vector2f;

import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.shapes.Circle;
import mbeb.opengldefault.shapes.Shape;

public abstract class LifeformEntity extends SceneEntity {

	private float healthpoints;

	private Shape hitCircle;

	public LifeformEntity(final SceneObject sceneObject, final float healthpoints, float radius) {
		super(sceneObject);
		this.healthpoints = healthpoints;
		hitCircle =
				new Circle(new Vector2f(sceneObject.getGlobalPosition().x, sceneObject.getGlobalPosition().z), radius);
	}

	public Shape getBounding() {
		hitCircle.setPosition(new Vector2f(getPosition().x, getPosition().z));
		return hitCircle;
	}

	public float getHealthpoints() {
		return healthpoints;
	}

	public void damage(float damage) {
		healthpoints -= damage;
		getSceneObject().setSelected(true);
	}
}
