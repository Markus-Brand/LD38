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

import java.util.function.Consumer;

public abstract class LifeformEntity extends SceneEntity {

	private float healthpoints;
	private float maxHealth;

	private boolean dead;

	private Shape hitCircle;

	private Consumer<LifeformEntity> deathListener;

	Vector3f healthBarOffset = new Vector3f();
	private HealthBarGUIElement healthBar;
	private final HealthBarGUI healthGui;
	
	private Camera camera = null; //used to project the healthbar on the screen

	public LifeformEntity(final SceneObject sceneObject, final float healthpoints, float radius, HealthBarGUI healthGui) {
		super(sceneObject);
		this.healthpoints = healthpoints;
		this.maxHealth = healthpoints;
		this.healthGui = healthGui;
		hitCircle =
				new Circle(new Vector2f(sceneObject.getGlobalPosition().x, sceneObject.getGlobalPosition().z), radius);
		dead = false;
		
		healthBar = new HealthBarGUIElement(getHealthBarSize(),
				                                   new Vector3f(0f, 1, 0.2f),
				                                   new Vector3f(1f, 0.4f, 0.2f),
				                                   new Vector3f(0f));
	}

	protected float getHealthBarSize() {
		return 0.2f;
	}

	public Shape getBounding() {
		hitCircle.setPosition(new Vector2f(getPosition().x, getPosition().z));
		return hitCircle;
	}

	public float getHealthpoints() {
		return healthpoints;
	}

	public void damage(float damage) {
		if (dead) {
			return;
		}
		healthpoints -= damage;
		System.out.println(healthpoints);
		if (healthpoints <= 0) {
			onDie();
		}
		healthBar.setHealth(healthpoints / maxHealth);
		
		getSceneObject().setSelected(true);
	}

	public void onDie() {
		dead = true;
		if(this.deathListener != null) {
			this.deathListener.accept(this);
		}
		if (healthGui != null) {
			healthGui.remove(healthBar);
		}
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
	
	public void showHealthBar(Camera camera) {
		if (healthGui != null) {
			healthGui.addGUIElement(healthBar);
		}
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

	public void setDeathListener(Consumer<LifeformEntity> deathListener) {
		this.deathListener = deathListener;
	}
}
