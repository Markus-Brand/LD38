package mbeb.opengldefault.sound;

import org.joml.Vector3f;

import mbeb.opengldefault.scene.behaviour.EntityParentBehaviour;
import mbeb.opengldefault.scene.entities.Entity;
import mbeb.opengldefault.scene.entities.IEntity;

/**
 * The abstract base for a sound listener and a sound source
 */
public abstract class SoundEntity extends Entity {

	private Vector3f position = new Vector3f();
	private Vector3f direction = new Vector3f(1, 0, 0);
	private float dopplerStrength = 1f;

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
	}

	@Override
	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	@Override
	public void update(double deltaTime) {
		Vector3f oldPosition = new Vector3f(position);
		super.update(deltaTime);
		Vector3f delta = position.sub(oldPosition, new Vector3f());

		if (delta.lengthSquared() < 0.0001f) {
			//no movement
			setSpeed(new Vector3f());
			return;
		}
		if (Math.abs(deltaTime) < 0.0001) {
			return; //no zero divide
		}
		delta.div((float) deltaTime);
		delta.mul(dopplerStrength);
		setSpeed(delta);
	}

	/**
	 * set the "speed" of this SoundEntity to a new value.
	 * The speed ist just used to calculate audio-effects, and does not actually move the object.
	 * 
	 * @param speed
	 */
	protected abstract void setSpeed(Vector3f speed);

	/**
	 * set a multiplier for the simulated speed of this SoundEntity
	 * (effectively scales the doppler effect for this SoundEntity)
	 * 
	 * @param dopplerStrength
	 */
	public void setDopplerStrength(float dopplerStrength) {
		this.dopplerStrength = dopplerStrength;
	}

	/**
	 * make this IEntity the child of another entity
	 * 
	 * @param parent
	 */
	public void attachTo(IEntity parent) {
		this.addBehaviour(0, new EntityParentBehaviour(parent));
	}
}
