package mbeb.lifeforms;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.CombinedBehaviour;
import mbeb.opengldefault.scene.behaviour.HeightSource;
import mbeb.opengldefault.scene.behaviour.SamuraiPlayerBehaviour;
import mbeb.opengldefault.scene.behaviour.WalkOnHeightMapBehaviour;

public class PlayerEntity extends LifeformEntity {

	private AnimationStateFacade animator;

	private float playerSpeed = 4f;

	public PlayerEntity(final SceneObject sceneObject, AnimationStateFacade animator,
			final float healthpoints, HeightSource heightSource) {
		super(sceneObject, healthpoints);
		this.animator = animator;
		addBehaviour(0, new CombinedBehaviour(
				new SamuraiPlayerBehaviour(),
				new WalkOnHeightMapBehaviour(heightSource, playerSpeed)));
	}

	public AnimationStateFacade getAnimator() {
		return animator;
	}

}
