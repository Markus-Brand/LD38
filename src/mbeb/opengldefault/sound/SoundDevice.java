package mbeb.opengldefault.sound;

import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

/**
 * The representation of your hardware speaker system
 */
public class SoundDevice {

	private static SoundDevice instance = null;

	/**
	 * @return the one sound device that the current machine owns
	 */
	public static SoundDevice getInstance() {
		if (instance == null) {
			instance = new SoundDevice();
		}
		return instance;
	}

	private long handle = NULL;
	private ALCCapabilities capabilities = null;

	private SoundDevice() {
	}

	/**
	 * @return the ALCCapabilites for this SoundDevice
	 */
	public ALCCapabilities getCapabilities() {
		if (capabilities == null) {
			capabilities = ALC.createCapabilities(getHandle());
		}
		return capabilities;
	}

	/**
	 * @return the OpenAL handle for this device
	 */
	private long getHandle() {
		if (handle == NULL) {
			handle = alcOpenDevice((ByteBuffer) null);
			if (handle == NULL) {
				throw new IllegalStateException("Failed to open the default OpenAL device.");
			}
		}
		return handle;
	}

	/**
	 * @return a new OpenAL context handle for this device
	 */
	public long createNewContext() {
		long context = alcCreateContext(getHandle(), (IntBuffer) null);
		if (context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		return context;
	}

	/**
	 * call this when finished playing sounds with OpenAL (will clean up all the stuff)
	 */
	public void close() {
		if (handle != NULL) {
			alcCloseDevice(handle);
		}
	}
}
