package mbeb.opengldefault.sound;

import static org.lwjgl.openal.AL10.*;

import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.IEntityConvertable;
import org.joml.Vector3f;

/**
 * a speaker inside a 3D-world
 */
public class SoundSource implements IEntityConvertable {

	private static final String TAG = "SoundSource";

	/** the openAL - handle for the speaker object */
	private final int sourceId;

	/**
	 * create a new speaker that is capable of playing multiple sounds
	 *
	 * @param loop
	 *            does this sound loop when playing?
	 * @param relativeToListener
	 *            whether this sound (and its positions) are seen as relative to the listener
	 */
	SoundSource(boolean loop, boolean relativeToListener) {
		this.sourceId = alGenSources();
		setMaxDistance(20f);
		setReferenceDistance(10f);
		setRolloffFactor(10f);
		setLooping(loop);
		setRelativeToListener(relativeToListener);
	}

	/**
	 * Sets whether the sound should start again after it finished
	 *
	 * @param loop
	 *            true -> looping;
	 *            false -> not looping
	 */
	private void setLooping(boolean loop) {
		setProperty(AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
	}

	/**
	 * Sets the coordinate system of the Sound
	 * true -> the sounds coordinate systems origin is in the listeners position
	 * false -> the sounds coordinate systems origin is in the worlds origin
	 *
	 * @param relativeToListener
	 *            whether this sound (and its positions) are seen as relative to the listener
	 */
	private void setRelativeToListener(boolean relativeToListener) {
		setProperty(AL_SOURCE_RELATIVE, relativeToListener ? AL_TRUE : AL_FALSE);
	}

	/**
	 * set the sound that should be played from this SoundSource. stops currently running sounds
	 *
	 * @param sound
	 */
	public void setSound(Sound sound) {
		stop();
		setProperty(AL_BUFFER, sound.getBufferId());
	}

	/**
	 * set the position of this SoundSource in global space
	 *
	 * @param position
	 *            a new position
	 */
	public void setPosition(Vector3f position) {
		Log.log(TAG, "Source Position: " + position);
		setProperty(AL_POSITION, position);
	}

	/**
	 * set the speed for this SoundSource. That does not move it, it is just used for doppler effect calculation
	 *
	 * @param speed
	 *            a new speed
	 */
	public void setSpeed(Vector3f speed) {
		setProperty(AL_VELOCITY, speed);
	}

	/**
	 * set the volume for this SoundSource, in range [0...1] (defaults to 1)
	 *
	 * @param gain
	 *            the new volume for this SoundSource
	 */
	public void setGain(float gain) {
		setProperty(AL_GAIN, gain);
	}

	/**
	 * Set the rolloff factor of this SoundSource.
	 * The higher the value the faster the sound will get quieter, at greater distance.
	 * The specific use of the factor is based on the AttenuationModel of the SoundEnvironment
	 *
	 * @param rolloff
	 *            the new rolloff factor
	 */
	public void setRolloffFactor(float rolloff) {
		setProperty(AL_ROLLOFF_FACTOR, rolloff);
	}

	/**
	 * Set the reference distance of this SoundSource.
	 * At this distance, the gain will be 1.
	 *
	 * @param referenceDistance
	 *            the new referenceDistance
	 */
	public void setReferenceDistance(float referenceDistance) {
		setProperty(AL_REFERENCE_DISTANCE, referenceDistance);
	}

	/**
	 * Set the max distance of this SoundSource.
	 * At this distance, the gain will not get any lower any more.
	 *
	 * @param maxDistance
	 *            the new maxDistance
	 */
	public void setMaxDistance(float maxDistance) {
		setProperty(AL_MAX_DISTANCE, maxDistance);
	}

	/**
	 * set an OpenAL-property and check for errors
	 *
	 * @param param
	 *            the property "name"
	 * @param value
	 *            the value for that property
	 */
	private void setProperty(int param, int value) {
		alSourcei(sourceId, param, value);
		ALErrors.checkForError(TAG, "alSourcei");
	}

	/**
	 * set an OpenAL-property and check for errors
	 *
	 * @param param
	 *            the property "name"
	 * @param value
	 *            the value for that property
	 */
	private void setProperty(int param, float value) {
		alSourcef(sourceId, param, value);
		ALErrors.checkForError(TAG, "alSourcef");
	}

	/**
	 * set an OpenAL-property and check for errors
	 *
	 * @param param
	 *            the property "name"
	 * @param value
	 *            the value for that property
	 */
	private void setProperty(int param, Vector3f value) {
		alSource3f(sourceId, param, value.x, value.y, value.z);
		ALErrors.checkForError(TAG, "alSource3f");
	}

	/**
	 * start playing the currently bound Sound, if not playing already
	 */
	public void play() {
		if (isPlaying()) {
			return;
		}
		alSourcePlay(sourceId);
		ALErrors.checkForError(TAG, "alSourcePlay");
	}

	/**
	 * @return whether this SoundSource currently plays a Sound
	 */
	public boolean isPlaying() {
		boolean isPlaying = alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
		ALErrors.checkForError(TAG, "alGetSourcei");
		return isPlaying;
	}

	/**
	 * pause playing the sound, if it is playing right now. The next {@link #play()} call will continue at the same
	 * position.
	 */
	public void pause() {
		if (isPlaying()) {
			return;
		}
		alSourcePause(sourceId);
		ALErrors.checkForError(TAG, "alSourcePause");
	}

	/**
	 * stop playing the sound, if it is playing right now. The next {@link #play()} call will start from the beginning
	 * again.
	 */
	public void stop() {
		if (isPlaying()) {
			return;
		}
		alSourceStop(sourceId);
		ALErrors.checkForError(TAG, "alSourceStop");
	}

	/**
	 * delete all the OpenAL - stuff related to this SoundSource
	 */
	public void cleanup() {
		stop();
		alDeleteSources(sourceId);
		ALErrors.checkForError(TAG, "alDeleteSources");
	}

	@Override
	public IEntity asNewEntity() {
		return new SoundSourceEntity(this);
	}
}
