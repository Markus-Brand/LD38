package mbeb.opengldefault.sound;

import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.scene.entities.IEntityConvertable;
import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/**
 * A 3d-oriented microphone in the sound world
 */
public class SoundListener implements IEntityConvertable {

	private static final String TAG = "SoundListener";

	/**
	 * create a listener at the worlds origin
	 */
	public SoundListener() {
		setPosition(new Vector3f());
		setSpeed(new Vector3f());
	}

	/**
	 * set the speed for the listener. That does not move it, it is just used for doppler effect calculation
	 * @param speed a new speed
	 */
	public void setSpeed(Vector3f speed) {
		setProperty(AL_VELOCITY, speed);
	}

	/**
	 * set the position of this listener in global space
	 * @param position a new position
	 */
	public void setPosition(Vector3f position) {
		Log.log(TAG, "listener position: " + position);
		setProperty(AL_POSITION, position);
	}

	/**
	 * set an OpenAL-property and check for errors
	 * @param name the property "name"
	 * @param value the value for that property
	 */
	private void setProperty(int name, Vector3f value) {
		alListener3f(name, value.x, value.y, value.z);
		ALErrors.checkForError(TAG, "alListener3f");
	}

	/**
	 * set the direction of this listener
	 * @param at the coordinates you are looking at
	 * @param up the current up vector
	 */
	public void setOrientation(Vector3f at, Vector3f up) {
		Log.log(TAG, "Listener lookAt: " + at);
		float[] data = new float[6];
		data[0] = at.x;
		data[1] = at.y;
		data[2] = at.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		alListenerfv(AL_ORIENTATION, data);
		ALErrors.checkForError(TAG, "alListenerfv");
	}

	@Override
	public SoundListenerEntity asNewEntity() {
		return new SoundListenerEntity(this);
	}
}
