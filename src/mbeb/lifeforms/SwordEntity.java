package mbeb.lifeforms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Vector2f;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.entities.SceneEntity;
import mbeb.opengldefault.shapes.Line;

public class SwordEntity extends SceneEntity {

	private float damage;
	private float range;
	private Map<LifeformEntity, Boolean> tarscheds;
	private boolean striking;
	private float timeStrinking;
	private float strokeTime;

	public SwordEntity(SceneObject sceneObject, float damage, float range, float strokeTime, SceneObject holderObject,
			AnimationStateFacade holderAnimator) {
		super(sceneObject);
		this.strokeTime = strokeTime;
		this.timeStrinking = 0;
		this.damage = damage;
		this.range = range;
		tarscheds = new HashMap<>();
		addBehaviour(0, new SwordBehaviour(holderObject, holderAnimator));
	}

	@Override
	public void update(double deltaTime) {
		if (striking) {
			timeStrinking += deltaTime;
			if (timeStrinking >= strokeTime) {
				stopStriking();
				timeStrinking = 0;
			}
		}
		super.update(deltaTime);
	}

	public void startStriking() {
		striking = true;
		for (Entry<LifeformEntity, Boolean> entry : getTarscheds().entrySet()) {
			entry.setValue(false);
		}
	}

	public void stopStriking() {
		striking = false;
	}

	public void addTarsched(LifeformEntity tarsched) {
		tarscheds.put(tarsched, false);
	}

	public Map<LifeformEntity, Boolean> getTarscheds() {
		return tarscheds;
	}

	public float getDamage() {
		return damage;
	}

	public float getRange() {
		return range;
	}

	public Line getBounding() {
		Vector2f start = new Vector2f(getPosition().x, getPosition().z);
		Vector2f end = new Vector2f(getDirection().x, getDirection().z).normalize().mul(range).add(start);
		return new Line(start, end);
	}

	/**
	 * @return the striking
	 */
	public boolean isStriking() {
		return striking;
	}

}
