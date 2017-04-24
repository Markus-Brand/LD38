package mbeb.lifeforms;

import org.joml.Vector2f;

import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.shapes.Circle;
import mbeb.opengldefault.shapes.Shape;
import org.joml.Vector3f;

public abstract class LifeformEntity extends SceneEntity {

	private float healthpoints;

	private boolean dead;

	private Shape hitCircle;

	Vector3f healthBarOffset = new Vector3f();

	public LifeformEntity(final SceneObject sceneObject, final float healthpoints, float radius) {
		super(sceneObject);
		this.healthpoints = healthpoints;
		hitCircle =
				new Circle(new Vector2f(sceneObject.getGlobalPosition().x, sceneObject.getGlobalPosition().z), radius);
		dead = false;
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
		System.out.println(healthpoints);
		if (healthpoints <= 0) {
			dead = true;
		}
		getSceneObject().setSelected(true);
	}

	public boolean isDead() {
		return dead;
	}

	public void setHealthBarOffset(Vector3f healthBarOffset) {
		this.healthBarOffset = healthBarOffset;
	}

	public Vector3f getHealthBarPosition() {
		return getPosition().add(healthBarOffset, new Vector3f());
	}
}
