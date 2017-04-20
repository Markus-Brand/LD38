package mbeb.opengldefault.sound;

import mbeb.opengldefault.scene.entities.Entity;
import org.joml.Vector3f;

/**
 * An Entity that can play sounds
 */
public class SoundSourceEntity extends Entity {

	private SoundSource soundSource;
	private Vector3f position;
	private Vector3f direction;

	public SoundSourceEntity(SoundSource soundSource) {
		this.soundSource = soundSource;
		position = new Vector3f();
		direction = new Vector3f();
	}

	public SoundSource getSoundSource() {
		return soundSource;
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Vector3f getDirection() {
		return direction;
	}

	@Override
	public void setPosition(Vector3f position) {
		this.position = position;
		soundSource.setPosition(this.position);
	}

	@Override
	public void setDirection(Vector3f direction) {
		this.direction = direction;
		//nothing to do here
	}
}
