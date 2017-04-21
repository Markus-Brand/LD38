package mbeb.opengldefault.sound;

import org.joml.Vector3f;

/**
 * An Entity that can listen to sounds
 */
public class SoundListenerEntity extends SoundEntity {

	private SoundListener soundListener;

	public SoundListenerEntity(SoundListener soundListener) {
		this.soundListener = soundListener;
	}

	public SoundListener getSoundListener() {
		return soundListener;
	}

	@Override
	public void setPosition(Vector3f position) {
		super.setPosition(position);
		soundListener.setPosition(position);
	}

	@Override
	public void setDirection(Vector3f direction) {
		super.setDirection(direction);
		soundListener.setOrientation(getPosition().add(direction, new Vector3f()), new Vector3f(0, 1, 0));
	}

	@Override
	protected void setSpeed(Vector3f speed) {
		soundListener.setSpeed(speed);
	}
}
