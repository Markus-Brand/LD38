package mbeb.lifeforms;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.scene.SceneObject;

/**
 * a fist
 */
public class EmptySword extends Sword {
	public EmptySword() {
		super(2, 0.2f, 0.5f, LootType.Wood, SwordType.LONG_SWORD);
	}
	@Override
	public SwordEntity spawnNew(SceneObject parent, SceneObject playerObject, AnimationStateFacade playerAnimatedRenderable) {
		final SceneObject swordObject = new SceneObject();
		parent.addSubObject(swordObject);
		return new SwordEntity(swordObject, damage, range, strokeTime, playerObject, playerAnimatedRenderable);
	}
}
