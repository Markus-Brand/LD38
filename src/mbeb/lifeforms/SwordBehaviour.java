package mbeb.lifeforms;

import java.util.Map.Entry;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.BoneTrackingBehaviour;
import mbeb.opengldefault.scene.behaviour.IBehaviour;
import mbeb.opengldefault.scene.entities.IEntity;

public class SwordBehaviour implements IBehaviour {

	BoneTrackingBehaviour boneTracking;

	public SwordBehaviour(SceneObject holderObject, AnimationStateFacade holderAnimator) {
		boneTracking = new BoneTrackingBehaviour(holderObject, holderAnimator.getAnimatedRenderable(), "Item.Right");
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		boneTracking.update(deltaTime, entity);

		SwordEntity sword = (SwordEntity) entity;

		if (!sword.isStriking()) {
			return;
		}

		for (Entry<LifeformEntity, Boolean> entry : sword.getTarscheds().entrySet()) {
			if (!entry.getValue() && entry.getKey().getBounding().intersectsShape(sword.getBounding())) {
				entry.getKey().damage(sword.getDamage());
				entry.setValue(true);
			}
		}

		sword.getTarscheds().keySet().removeIf(LifeformEntity::isDead);

	}

}
