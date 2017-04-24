package mbeb.lifeforms;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.entities.*;

public class ChestEntity extends SceneEntity {

	private final AnimationStateFacade animator;
	private final float interactionRadius;

	public ChestEntity(final SceneObject sceneObject, final AnimationStateFacade animator, final float interactionRadius) {
		super(sceneObject);

		this.animator = animator;
		this.interactionRadius = interactionRadius;
	}

}
