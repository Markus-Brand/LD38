package mbeb.opengldefault.sound;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by erik on 20.04.17.
 */
public class SoundManager {
	private long device;

	private long context;

	private SoundListener listener;

	private final List<SoundBuffer> soundBufferList;

	private final Map<String, SoundSource> soundSourceMap;

	public static void main(String[] args) {
		SoundManager mgr = new SoundManager();
		SoundBuffer buf;
		try {
			mgr.init();
			buf =new SoundBuffer("sounds/soundtrackSmall.ogg");
			mgr.addSoundBuffer(buf);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		mgr.addSoundSource("speaker1", new SoundSource(true, false));
		mgr.getSoundSource("speaker1").setBuffer(buf.getBufferId());

		mgr.playSoundSource("speaker1");

		mgr.getSoundSource("speaker1").setGain(0.5f);

		mgr.cleanup();
	}

	public SoundManager() {
		soundBufferList = new ArrayList<>();
		soundSourceMap = new HashMap<>();
	}

	public void init() throws Exception {
		this.device = alcOpenDevice((ByteBuffer) null);
		if (device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		this.context = alcCreateContext(device, (IntBuffer) null);
		if (context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
	}

	public void addSoundSource(String name, SoundSource soundSource) {
		this.soundSourceMap.put(name, soundSource);
	}

	public SoundSource getSoundSource(String name) {
		return this.soundSourceMap.get(name);
	}

	public void playSoundSource(String name) {
		SoundSource soundSource = this.soundSourceMap.get(name);
		if (soundSource != null && !soundSource.isPlaying()) {
			soundSource.play();
		}
	}

	public void removeSoundSource(String name) {
		this.soundSourceMap.remove(name);
	}

	public void addSoundBuffer(SoundBuffer soundBuffer) {
		this.soundBufferList.add(soundBuffer);
	}

	public SoundListener getListener() {
		return this.listener;
	}

	public void setListener(SoundListener listener) {
		this.listener = listener;
	}

	public void updateListenerPosition(Vector3f at, Vector3f up) {
		listener.setOrientation(at, up);
	}

	public void setAttenuationModel(int model) {
		alDistanceModel(model);
	}

	public void cleanup() {
		for (SoundSource soundSource : soundSourceMap.values()) {
			soundSource.cleanup();
		}
		soundSourceMap.clear();
		for (SoundBuffer soundBuffer : soundBufferList) {
			soundBuffer.cleanup();
		}
		soundBufferList.clear();
		if (context != NULL) {
			alcDestroyContext(context);
		}
		if (device != NULL) {
			alcCloseDevice(device);
		}
	}
}