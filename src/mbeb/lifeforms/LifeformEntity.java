package mbeb.lifeforms;

import java.util.function.*;

import org.joml.*;

import mbeb.ld38.*;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.shapes.*;

public abstract class LifeformEntity extends SceneEntity {

	private float healthpoints;
	private final float maxHealth;

	private boolean dead;

	private final Shape hitCircle;

	private Consumer<LifeformEntity> deathListener;

	Vector3f healthBarOffset = new Vector3f();
	private final HealthBarGUIElement healthBar;
	private final HealthBarGUI healthGui;

	private Camera camera = null; //used to project the healthbar on the screen

	public LifeformEntity(final SceneObject sceneObject, final float healthpoints, final float radius, final HealthBarGUI healthGui) {
		super(sceneObject);
		this.healthpoints = healthpoints;
		this.maxHealth = healthpoints;
		this.healthGui = healthGui;
		hitCircle = new Circle(new Vector2f(sceneObject.getGlobalPosition().x, sceneObject.getGlobalPosition().z), radius);
		dead = false;

		healthBar = new HealthBarGUIElement(getHealthBarSize(), new Vector3f(0f, 1, 0.2f), new Vector3f(1f, 0.4f, 0.2f), new Vector3f(0.2f));
	}

	protected float getHealthBarSize() {
		return 0.2f;
	}

	public Shape getBounding() {
		hitCircle.setPosition(new Vector2f(getPosition().x, getPosition().z));
		return hitCircle;
	}

	public void resetHealth() {
		this.healthpoints = maxHealth;
		this.damage(0.0f);
	}

	public float getHealthpoints() {
		return healthpoints;
	}

	public void damage(final float damage) {
		if (dead) {
			return;
		}
		healthpoints -= damage;
		if (healthpoints <= 0) {
			onDie();
		}
		healthBar.setHealth(healthpoints / maxHealth);

		getSceneObject().setSelected(true);
	}

	public void onDie() {
		dead = true;
		if (this.deathListener != null) {
			this.deathListener.accept(this);
		}
		if (healthGui != null) {
			healthGui.remove(healthBar);
		}

	}

	public boolean isDead() {
		return dead;
	}

	public void setHealthBarOffset(final Vector3f healthBarOffset) {
		this.healthBarOffset = healthBarOffset;
	}

	public Vector3f getHealthBarPosition() {
		return getPosition().add(healthBarOffset, new Vector3f());
	}

	public void showHealthBar(final Camera camera) {
		if (healthGui != null) {
			healthGui.addGUIElement(healthBar);
		}
		setCamera(camera);
	}

	@Override
	public void update(final double deltaTime) {
		super.update(deltaTime);
		healthBar.update(deltaTime);

		if (camera == null) {
			healthBar.setPositionRelativeToScreen(0.01f, 0.99f);
		} else {
			final Vector3f screenSpace = camera.getPositionOnScreen(getHealthBarPosition());
			final Vector2f hudSpace = new Vector2f((screenSpace.x + 1) / 2, (screenSpace.y + 1) / 2);
			healthBar.setPositionRelativeToScreen(hudSpace);
		}
	}

	public void setCamera(final Camera camera) {
		this.camera = camera;
	}

	public void setDeathListener(final Consumer<LifeformEntity> deathListener) {
		this.deathListener = deathListener;
	}

	public void knockBack(Vector3f direction, float strength) {
		Vector3f target = direction.mul(strength, new Vector3f()).add(getPosition());
		setPosition(target);
	}
}
