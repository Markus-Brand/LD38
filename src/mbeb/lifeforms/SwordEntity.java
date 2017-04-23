package mbeb.lifeforms;

import java.util.LinkedList;

import org.joml.Vector2f;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.entities.SceneEntity;
import mbeb.opengldefault.shapes.Line;

public class SwordEntity extends SceneEntity {

	private float damage;
	private float range;
	private LinkedList<LifeformEntity> tarscheds;

	public SwordEntity(SceneObject sceneObject, float damage, float range, SceneObject holderObject,
			AnimationStateFacade holderAnimator) {
		super(sceneObject);
		this.damage = damage;
		this.range = range;
		tarscheds = new LinkedList<>();
		addBehaviour(0, new SwordBehaviour(holderObject, holderAnimator));
	}

	public void addTarsched(LifeformEntity tarsched) {
		tarscheds.add(tarsched);
	}

	public LinkedList<LifeformEntity> getTarscheds() {
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
}
