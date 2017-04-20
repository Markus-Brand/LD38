package mbeb.opengldefault.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/**
 * A 3d-oriented microphone in the sound world
 */
public class SoundListener {

	private static final String TAG = "SoundListener";

	public SoundListener() {
		this(new Vector3f());
	}

	public SoundListener(Vector3f position) {
		setPosition(position);
		setSpeed(new Vector3f());
	}

	public void setSpeed(Vector3f speed) {
		setProperty(AL_VELOCITY, speed);
	}

	public void setPosition(Vector3f position) {
		setProperty(AL_POSITION, position);
	}

	private void setProperty(int name, Vector3f value) {
		alListener3f(name, value.x, value.y, value.z);
		ALErrors.checkForError(TAG, "alListener3f");
	}

	public void setOrientation(Vector3f at, Vector3f up) {
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
}
