package mbeb.opengldefault.sound;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import mbeb.opengldefault.logging.Log;

/**
 * 1:1 Mapping of an ogg file
 */
public class Sound {

	private static final String TAG = "Sound";

	/** which sound file to load */
	private final String fileName;

	/** the openAL - handle for that sound object (-1 if not created yet) */
	private int bufferId;

	/** the buffer that contains the sound data in pcm-format */
	private ShortBuffer pcm;

	/**
	 * load a sound file from resources
	 * 
	 * @param fileName
	 *            the file path (sounds/*.ogg)
	 * @throws Exception
	 */
	Sound(String fileName) {
		this.fileName = fileName;
		this.bufferId = -1;
		pcm = null;
	}

	/**
	 * create the OpenAL SoundBuffer - object
	 */
	private void create() {
		this.bufferId = alGenBuffers();
		try(STBVorbisInfo info = STBVorbisInfo.malloc()) {
			pcm = readVorbis(fileName, 32 * 1024, info);

			int format = info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
			alBufferData(bufferId, format, pcm, info.sample_rate());
		} catch(Exception e) {
			Log.error(TAG, "failed to load sound file", e);
		}
	}

	/**
	 * @return whether this sound is already loaded
	 */
	public boolean created() {
		return bufferId >= 0;
	}

	/**
	 * Get the OpenAL SoundBuffer-handle for this object.
	 * Create it if not existing yet
	 * 
	 * @return a soundBuffer handle
	 */
	public int getBufferId() {
		if (!created()) {
			create();
		}
		return bufferId;
	}

	/**
	 * destroys all OpenAL-stuff that belongs to this Object
	 */
	public void cleanup() {
		if (!created()) {
			return;
		}
		alDeleteBuffers(this.bufferId);
		if (pcm != null) {
			MemoryUtil.memFree(pcm);
		}
	}

	/**
	 * convert an ogg-vorbis input source to a Buffer of pcm-data
	 * 
	 * @param resource
	 *            the source path of the ogg-file
	 * @param bufferSize
	 *            how big the io-buffer should be
	 * @param info
	 *            the info object to store meta info of the file in
	 * @return a buffer containing the pcm data
	 * @throws Exception
	 *             if anything goes wrong with the io (file not found or corrupted)
	 */
	private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer vorbis = ioResourceToByteBuffer(resource, bufferSize);
			IntBuffer error = stack.mallocInt(1);
			long decoder = stb_vorbis_open_memory(vorbis, error, null);
			if (decoder == NULL) {
				throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
			}

			stb_vorbis_get_info(decoder, info);

			int channels = info.channels();

			int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

			pcm = MemoryUtil.memAllocShort(lengthSamples);

			pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
			stb_vorbis_close(decoder);

			return pcm;
		}
	}

	/**
	 * Reads the specified resource and returns the raw data as a ByteBuffer.
	 *
	 * @param resource
	 *            the resource to read
	 * @param bufferSize
	 *            the initial buffer size
	 * @return the resource data
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		try(InputStream source = Sound.class.getClassLoader().getResourceAsStream(resource); ReadableByteChannel rbc = Channels.newChannel(source)) {
			buffer = createByteBuffer(bufferSize);

			while(true) {
				int bytes = rbc.read(buffer);
				if (bytes == -1) {
					break;
				}
				if (buffer.remaining() == 0) {
					buffer = resizeBuffer(buffer, buffer.capacity() * 2);
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	/**
	 * Utility function to "enlarge" a buffer (create a new one and put all data into it).
	 * Think of ArrayLists, they do mostly the same.
	 * 
	 * @param buffer
	 *            the old and probably full buffer
	 * @param newCapacity
	 *            the desired new capacity
	 * @return a Buffer with the new capacity and the old data
	 */
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
}
