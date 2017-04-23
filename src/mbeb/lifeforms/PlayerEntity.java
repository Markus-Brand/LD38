package mbeb.lifeforms;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.CombinedBehaviour;
import mbeb.opengldefault.scene.behaviour.HeightSource;
import mbeb.opengldefault.scene.behaviour.SamuraiPlayerBehaviour;
import mbeb.opengldefault.scene.behaviour.WalkOnHeightMapBehaviour;

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
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		swordEntity.update(deltaTime);
	}

	public void startStroke() {
		if (!swordEntity.isStriking()) {
			swordEntity.startStriking();
		}
	}

	public AnimationStateFacade getAnimator() {
		return animator;
	}

	public SwordEntity getSword() {
		return swordEntity;
	}

	public void addTarsched(LifeformEntity tarsched) {
		swordEntity.addTarsched(tarsched);
	}
}
