package mbeb.opengldefault.sound;

import static org.lwjgl.openal.AL10.*;

import org.joml.Vector3f;

/**
 * a speaker inside a 3D-world
 */
public class SoundSource {

	private static final String TAG = "SoundSource";

	/** the openAL - handle for the speaker object */
	private final int sourceId;

	/**
	 * create a new speaker that is capable of playing multiple sounds
	 * 
	 * @param loop
	 *            does this sound loop when playing?
	 * @param relative
	 *            whether this sound (and its positions) are seen as relative to the listener
	 */
	SoundSource(boolean loop, boolean relative) {
		this.sourceId = alGenSources();
		if (loop) {
			setProperty(AL_LOOPING, AL_TRUE);
		}
		if (relative) {
			setProperty(AL_SOURCE_RELATIVE, AL_TRUE);
		}
	}

	/**
	 * set the sound that should be played from this speaker. stops currently running sounds
	 * 
	 * @param sound
	 */
	public void setSound(Sound sound) {
		stop();
		setProperty(AL_BUFFER, sound.getBufferId());
	}

	public void setPosition(Vector3f position) {
		setProperty(AL_POSITION, position);
	}

	public void setSpeed(Vector3f speed) {
		setProperty(AL_VELOCITY, speed);
	}

	public void setGain(float gain) {
		setProperty(AL_GAIN, gain);
	}

	public void setProperty(int param, int value) {
		alSourcei(sourceId, param, value);
		ALErrors.checkForError(TAG, "alSourcei");
	}

	public void setProperty(int param, float value) {
		alSourcef(sourceId, param, value);
		ALErrors.checkForError(TAG, "alSourcef");
	}

	public void setProperty(int param, Vector3f value) {
		alSource3f(sourceId, param, value.x, value.y, value.z);
		ALErrors.checkForError(TAG, "alSource3f");
	}

	public void play() {
		if (isPlaying()) {
			return;
		}
		alSourcePlay(sourceId);
		ALErrors.checkForError(TAG, "alSourcePlay");
	}

	public boolean isPlaying() {
		boolean isPlaying = alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
		ALErrors.checkForError(TAG, "alGetSourcei");
		return isPlaying;
	}

	public void pause() {
		alSourcePause(sourceId);
		ALErrors.checkForError(TAG, "alSourcePause");
	}

	public void stop() {
		alSourceStop(sourceId);
		ALErrors.checkForError(TAG, "alSourceStop");
	}

	public void cleanup() {
		stop();
		alDeleteSources(sourceId);
		ALErrors.checkForError(TAG, "alDeleteSources");
	}
}
