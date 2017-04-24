package mbeb.lifeforms;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.CombinedBehaviour;
import mbeb.opengldefault.scene.behaviour.HeightSource;
import mbeb.opengldefault.scene.behaviour.SamuraiPlayerBehaviour;
import mbeb.opengldefault.scene.behaviour.WalkOnHeightMapBehaviour;
import org.joml.Vector3f;

public class PlayerEntity extends LifeformEntity {

	private AnimationStateFacade animator;

	private SwordEntity swordEntity;

	private float playerSpeed = 4f;

	public PlayerEntity(float radius, final SceneObject sceneObject, AnimationStateFacade animator,
			final float healthpoints, HeightSource heightSource, SwordEntity swordEntity) {
		super(sceneObject, healthpoints, radius);
		this.animator = animator;
		this.swordEntity = swordEntity;
		addBehaviour(0, new CombinedBehaviour(
				new SamuraiPlayerBehaviour(),
				new WalkOnHeightMapBehaviour(heightSource, playerSpeed)));
		setHealthBarOffset(new Vector3f(0, 2, 0));
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		swordEntity.update(deltaTime);
	}

	public void startStroke() {
		if (!swordEntity.isStriking()) {
			swordEntity.startStriking();
			getAnimator().ensureRunning("Pierce", true, false);
			getAnimator().ensureRunning("Pierce", false, false);
		}
	}

	public AnimationStateFacade getAnimator() {
		return animator;
	}

	public void setSword(Sword sword) {
		setSwordEntity(sword.spawnNew(swordEntity.getSceneObject().getParent(), getSceneObject(), animator));
	}

	public void setSwordEntity(SwordEntity swordEntity) {
		this.swordEntity.getSceneObject().removeSelf();
		this.swordEntity = swordEntity;
	}

	public SwordEntity getSword() {
		return swordEntity;
	}

	public void addTarsched(LifeformEntity tarsched) {
		swordEntity.addTarsched(tarsched);
	}
}
