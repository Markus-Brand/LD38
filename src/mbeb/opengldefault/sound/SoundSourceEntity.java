package mbeb.opengldefault.sound;

import org.joml.Vector3f;

/**
 * An Entity that can play sounds
 */
public class SoundSourceEntity extends SoundEntity {

	private SoundSource soundSource;

	public SoundSourceEntity(SoundSource soundSource) {
		this.soundSource = soundSource;
	}

	public SoundSource getSoundSource() {
		return soundSource;
	}

	@Override
	public void setPosition(Vector3f position) {
		super.setPosition(position);
		soundSource.setPosition(position);
	}

	@Override
	protected void setSpeed(Vector3f speed) {
		soundSource.setSpeed(speed);
	}
}
