package mbeb.lifeforms;

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

		for (LifeformEntity tarsched : sword.getTarscheds()) {
			if (tarsched.getBounding().intersectsShape(sword.getBounding())) {
				tarsched.damage(sword.getDamage());
			} else {
				tarsched.getSceneObject().setSelected(false);
			}
		}
	}

}
