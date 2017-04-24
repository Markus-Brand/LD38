package mbeb.lifeforms;

import mbeb.ld38.HealthBarGUI;
import mbeb.ld38.HealthBarGUIElement;
import mbeb.opengldefault.camera.Camera;
import org.joml.Vector2f;

import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.shapes.Circle;
import mbeb.opengldefault.shapes.Shape;
import org.joml.Vector3f;

public abstract class LifeformEntity extends SceneEntity {

	private float healthpoints;
	private float maxHealth;

	private boolean dead;

	private Shape hitCircle;

	Vector3f healthBarOffset = new Vector3f();
	private HealthBarGUIElement healthBar;
	
	private Camera camera = null; //used to project the healthbar on the screen

	public LifeformEntity(final SceneObject sceneObject, final float healthpoints, float radius) {
		super(sceneObject);
		this.healthpoints = healthpoints;
		this.maxHealth = healthpoints;
		hitCircle =
				new Circle(new Vector2f(sceneObject.getGlobalPosition().x, sceneObject.getGlobalPosition().z), radius);
		dead = false;
		
		healthBar = new HealthBarGUIElement(0.8f,
				                                   new Vector3f(0f, 0.3f, 1),
				                                   new Vector3f(1, 1, 0.4f),
				                                   new Vector3f(0.01f));
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
		healthBar.setHealth(healthpoints / maxHealth);
		
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
	
	public void showHealthBar(HealthBarGUI gui, Camera camera) {
		gui.addGUIElement(healthBar);
		setCamera(camera);
	}
	
	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		healthBar.update(deltaTime);
		
		if (camera == null) {
			healthBar.setPositionRelativeToScreen(0.01f, 0.99f);
		} else {
			Vector3f screenSpace = camera.getPositionOnScreen(getHealthBarPosition());
			Vector2f hudSpace = new Vector2f((screenSpace.x + 1) / 2, (screenSpace.y + 1) / 2);
			healthBar.setPositionRelativeToScreen(hudSpace);
		}
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
