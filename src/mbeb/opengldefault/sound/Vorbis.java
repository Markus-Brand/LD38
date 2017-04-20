package mbeb.opengldefault.sound;

import static java.lang.Math.*;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.openal.SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT;
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

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.stb.STBVorbisInfo;

/**
 * STB Vorbis demo.
 * <p>
 * Playback will pause while handling window events. In a real application, this can be fixed by running the decoder in
 * a different thread.
 * </p>
 */
public final class Vorbis {

	private Vorbis() {
	}

	public static void main(String[] args) {
		String filePath;
		if (args.length == 0) {
			System.out
					.println("Use 'ant demo -Dclass=org.lwjgl.demo.stb.Vorbis -Dargs=<path>' to load a different Ogg Vorbis file.\n");
			filePath = "sounds/soundtrackSmall.ogg";
		} else {
			filePath = args[0];
		}

		long device = alcOpenDevice((ByteBuffer) null);
		if (device == NULL) {
			throw new IllegalStateException("Failed to open the default device.");
		}

		ALCCapabilities deviceCaps = ALC.createCapabilities(device);

		long context = alcCreateContext(device, (IntBuffer) null);
		if (context == NULL) {
			throw new IllegalStateException("Failed to create an OpenAL context.");
		}

		alcSetThreadContext(context);
		AL.createCapabilities(deviceCaps);

		int source = alGenSources();
		alSourcei(source, AL_DIRECT_CHANNELS_SOFT, AL_TRUE);

		IntBuffer buffers = BufferUtils.createIntBuffer(2);
		alGenBuffers(buffers);

		Decoder decoder = null;
		try {
			decoder = new Decoder(filePath);

			if (!decoder.play(source, buffers)) {
				System.err.println("Playback failed.");
			}

			long startTime = System.currentTimeMillis();
			while(System.currentTimeMillis() - startTime < 10000) {
				if (!decoder.update(source, true)) {
					System.err.println("Playback failed.");
				}
			}
		} finally {
			if (decoder != null && decoder.handle >= 0) {
				stb_vorbis_close(decoder.handle);
			}

			alDeleteBuffers(buffers);
			alDeleteSources(source);

			alcSetThreadContext(NULL);
			alcDestroyContext(context);
			alcCloseDevice(device);
		}
	}

	private static class Decoder {

		private static final int BUFFER_SIZE = 1024 * 4;

		final ByteBuffer vorbis;

		final long handle;
		final int channels;
		final int sampleRate;
		final int format;

		final int lengthSamples;
		final float lengthSeconds;

		final ShortBuffer pcm;

		int samplesLeft;

		Decoder(String filePath) {
			try {
				vorbis = ioResourceToByteBuffer(filePath, 256 * 1024);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}

			IntBuffer error = BufferUtils.createIntBuffer(1);
			handle = stb_vorbis_open_memory(vorbis, error, null);
			if (handle == NULL) {
				throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
			}

			try(STBVorbisInfo info = STBVorbisInfo.malloc()) {
				Decoder.getInfo(handle, info);
				this.channels = info.channels();
				this.sampleRate = info.sample_rate();
			}

			this.format = getFormat(channels);

			this.lengthSamples = stb_vorbis_stream_length_in_samples(handle);
			this.lengthSeconds = stb_vorbis_stream_length_in_seconds(handle);

			this.pcm = BufferUtils.createShortBuffer(BUFFER_SIZE);

			samplesLeft = lengthSamples;
		}

		private static void getInfo(long decoder, STBVorbisInfo info) {
			System.out.println("stream length, samples: " + stb_vorbis_stream_length_in_samples(decoder));
			System.out.println("stream length, seconds: " + stb_vorbis_stream_length_in_seconds(decoder));

			System.out.println();

			stb_vorbis_get_info(decoder, info);

			System.out.println("channels = " + info.channels());
			System.out.println("sampleRate = " + info.sample_rate());
			System.out.println("maxFrameSize = " + info.max_frame_size());
			System.out.println("setupMemoryRequired = " + info.setup_memory_required());
			System.out.println("setupTempMemoryRequired() = " + info.setup_temp_memory_required());
			System.out.println("tempMemoryRequired = " + info.temp_memory_required());
		}

		private static int getFormat(int channels) {
			switch(channels) {
				case 1:
					return AL_FORMAT_MONO16;
				case 2:
					return AL_FORMAT_STEREO16;
				default:
					throw new UnsupportedOperationException("Unsupported number of channels: " + channels);
			}
		}

		private boolean stream(int buffer) {
			int samples = 0;

			while(samples < BUFFER_SIZE) {
				pcm.position(samples);
				int samplesPerChannel = stb_vorbis_get_samples_short_interleaved(handle, channels, pcm);
				if (samplesPerChannel == 0) {
					break;
				}

				samples += samplesPerChannel * channels;
			}

			if (samples == 0) {
				return false;
			}

			pcm.position(0);
			alBufferData(buffer, format, pcm, sampleRate);
			samplesLeft -= samples / channels;

			return true;
		}

		float getProgress() {
			return 1.0f - samplesLeft / (float) lengthSamples;
		}

		float getProgressTime(float progress) {
			return progress * lengthSeconds;
		}

		void rewind() {
			stb_vorbis_seek_start(handle);
			samplesLeft = lengthSamples;
		}

		void skip(int direction) {
			seek(min(max(0, stb_vorbis_get_sample_offset(handle) + direction * sampleRate), lengthSamples));
		}

		void skipTo(float offset0to1) {
			seek(round(lengthSamples * offset0to1));
		}

		private void seek(int sample_number) {
			stb_vorbis_seek(handle, sample_number);
			samplesLeft = lengthSamples - sample_number;
		}

		boolean play(int source, IntBuffer buffers) {
			for (int i = 0; i < buffers.limit(); i++) {
				if (!stream(buffers.get(i))) {
					return false;
				}
			}

			alSourceQueueBuffers(source, buffers);
			alSourcePlay(source);

			return true;
		}

		boolean update(int source, boolean loop) {
			int processed = alGetSourcei(source, AL_BUFFERS_PROCESSED);

			for (int i = 0; i < processed; i++) {
				int buffer = alSourceUnqueueBuffers(source);

				if (!stream(buffer)) {
					boolean shouldExit = true;

					if (loop) {
						rewind();
						shouldExit = !stream(buffer);
					}

					if (shouldExit) {
						return false;
					}
				}
				alSourceQueueBuffers(source, buffer);
			}

			if (processed == 2) {
				alSourcePlay(source);
			}

			return true;
		}

	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
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

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try(SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = createByteBuffer((int) fc.size() + 1);
				while(fc.read(buffer) != -1) {
					;
				}
			}
		} else {
			try(InputStream source = Vorbis.class.getClassLoader().getResourceAsStream(resource);
					ReadableByteChannel rbc = Channels.newChannel(source)) {
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
		}

		buffer.flip();
		return buffer;
	}
}
