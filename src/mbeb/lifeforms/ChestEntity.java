package mbeb.lifeforms;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.sound.*;

public class ChestEntity extends SceneEntity {

	private final AnimationStateFacade animator;
	SoundSource chestSoundSource;

	public ChestEntity(final SceneObject sceneObject, final AnimationStateFacade animator, final SoundEnvironment soundEnvironment) {
		super(sceneObject);

		this.animator = animator;
		initSound(soundEnvironment);
	}

	private void initSound(final SoundEnvironment soundEnvironment) {
		final Sound chestSound = soundEnvironment.createSound("chest");
		this.chestSoundSource = soundEnvironment.createSoundSource(false, false);
		chestSoundSource.setSound(chestSound);
		chestSoundSource.setPosition(this.getPosition());
	}

	public AnimationStateFacade getAnimator() {
		return animator;
	}
}
