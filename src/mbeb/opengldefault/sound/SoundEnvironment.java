package mbeb.opengldefault.sound;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import java.util.*;

import org.lwjgl.openal.*;

/**
 * A Context for OpenAL calculations. Sounds and SoundSources belong to one.
 */
public class SoundEnvironment {

	private static final String TAG = "SoundEnvironment";

	/** the device to play on */
	private long device;

	/** the context handle (unique id of this SoundEnvironment) */
	private long context;

	/** the listener of this environment */
	private SoundListener listener;

	/** all the sounds that are loaded here */
	private final List<Sound> soundList;

	/** all the soundSources that are inside this Environment */
	private final List<SoundSource> soundSourceList;

	/**
	 * Sound testing method. Set a breakpoint on the cleanup and listen :)
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		final SoundEnvironment env = new SoundEnvironment();
		final Sound sound = env.createSound("sounds/soundtrackSmall.ogg");
		final SoundSource source = env.createSoundSource(true, false);
		source.setSound(sound);

		source.play();

		env.cleanup();
	}

	/**
	 * Create a new SoundEnvironment and init it immediately
	 */
	public SoundEnvironment() {
		soundList = new ArrayList<>();
		soundSourceList = new ArrayList<>();
		init();
		setAttenuationModel(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
	}

	/**
	 * create the OpenAL - context
	 */
	private void init() {
		this.device = alcOpenDevice((ByteBuffer) null);
		if (device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		final ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		this.context = alcCreateContext(device, (IntBuffer) null);
		if (context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		makeCurrent();
		AL.createCapabilities(deviceCaps);
		ALErrors.checkForError(TAG, "init");
	}

	/**
	 * load a new Sound file into this context
	 *
	 * @param filename
	 *            the name of the sound file to load
	 * @return an object representing that sound
	 */
	public Sound createSound(final String filename) {
		makeCurrent();
		final Sound sound = new Sound("sounds/" + filename + ".ogg");
		soundList.add(sound);
		return sound;
	}

	/**
	 * create a new SoundSource that can play sounds inside this Environment
	 *
	 * @param loop
	 *            whether the sound should loop automatically when it ended
	 * @param relative
	 *            true to see the position values of this SoundSource as always relative to the listener
	 *            (could be useful for "screen-space-sounds like background music or HUD/GUI-sounds)
	 * @return an Object that can play sounds
	 */
	public SoundSource createSoundSource(final boolean loop, final boolean relative) {
		makeCurrent();
		final SoundSource source = new SoundSource(loop, relative);
		soundSourceList.add(source);
		return source;
	}

	/**
	 * activates this soundEnvironment
	 */
	public void makeCurrent() {
		alcMakeContextCurrent(context);
		//ALErrors.checkForError(TAG, "alcMakeContextCurrent");
	}

	/**
	 * @return the Object that represents the Listener inside this Environment
	 */
	public SoundListener getListener() {
		if (listener == null) {
			listener = new SoundListener();
		}
		return listener;
	}

	/**
	 * set the calculation model for attenuation inside this Environment
	 *
	 * @param model
	 */
	public void setAttenuationModel(final int model) {
		alDistanceModel(model);
		ALErrors.checkForError(TAG, "alDistanceModel");
	}

	/**
	 * delete everything that has to do with OpenAL (clean up all the buffers and such)
	 */
	public void cleanup() {
		makeCurrent();
		soundSourceList.forEach(SoundSource::cleanup);
		soundSourceList.clear();
		soundList.forEach(Sound::cleanup);
		soundList.clear();
		if (context != NULL) {
			alcDestroyContext(context);
		}
		if (device != NULL) {
			alcCloseDevice(device);
		}
	}
}
