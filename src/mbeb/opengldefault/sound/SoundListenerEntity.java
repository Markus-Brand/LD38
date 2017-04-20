package mbeb.opengldefault.sound;

import org.joml.Vector3f;

import mbeb.opengldefault.scene.behaviour.EntityParentBehaviour;
import mbeb.opengldefault.scene.entities.Entity;
import mbeb.opengldefault.scene.entities.IEntity;

/**
 * An Entity that can listen to sounds
 */
public class SoundListenerEntity extends Entity {

	private SoundListener soundListener;
	private Vector3f position;
	private Vector3f direction;

	public SoundListenerEntity(SoundListener soundListener) {
		this.soundListener = soundListener;
		position = new Vector3f();
		direction = new Vector3f();
	}

	public SoundListener getSoundListener() {
		return soundListener;
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
		soundListener.setPosition(this.position);
	}

	@Override
	public void setDirection(Vector3f direction) {
		this.direction = direction;
		soundListener.setOrientation(getPosition().add(direction, new Vector3f()), new Vector3f(0, 1, 0));
	}

	/**
	 * make this IEntity the child of a
	 * 
	 * @param parent
	 */
	public void attachTo(IEntity parent) {
		this.addBehaviour(0, new EntityParentBehaviour(parent));
	}
}
