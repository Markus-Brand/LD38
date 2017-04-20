package mbeb.opengldefault.sound;

import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 */
public class SoundEnvironment {

	private static final String TAG = "SoundEnvironment";

	private long device;

	private long context;

	private SoundListener listener;

	private final List<Sound> soundList;

	private final List<SoundSource> soundSourceList;

	public static void main(String[] args) {
		SoundEnvironment env = new SoundEnvironment();
		Sound sound = env.createSound("sounds/soundtrackSmall.ogg");
		SoundSource source = env.createSoundSource(true, false);
		source.setSound(sound);

		source.play();

		source.setGain(0.5f);

		env.cleanup();
	}

	public SoundEnvironment() {
		soundList = new ArrayList<>();
		soundSourceList = new ArrayList<>();
		init();
	}

	private void init() {
		this.device = alcOpenDevice((ByteBuffer) null);
		if (device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		this.context = alcCreateContext(device, (IntBuffer) null);
		if (context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		makeCurrent();
		AL.createCapabilities(deviceCaps);
		ALErrors.checkForError(TAG, "init");
	}

	public Sound createSound(String filename) {
		makeCurrent();
		Sound sound = new Sound(filename);
		soundList.add(sound);
		return sound;
	}

	public SoundSource createSoundSource(boolean loop, boolean relative) {
		makeCurrent();
		SoundSource source = new SoundSource(loop, relative);
		soundSourceList.add(source);
		return source;
	}

	/**
	 * activates this soundEnvironment
	 */
	public void makeCurrent() {
		alcMakeContextCurrent(context);
		ALErrors.checkForError(TAG, "alcMakeContextCurrent");
	}

	public SoundListener getListener() {
		if (listener == null) {
			listener = new SoundListener();
		}
		return listener;
	}

	public void updateListenerPosition(Vector3f at, Vector3f up) {
		getListener().setOrientation(at, up);
	}

	public void setAttenuationModel(int model) {
		alDistanceModel(model);
		ALErrors.checkForError(TAG, "alDistanceModel");
	}

	public void cleanup() {
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
		ALErrors.checkForError(TAG, "cleanup");
	}
}