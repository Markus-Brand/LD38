package mbeb.opengldefault.sound;

import java.io.InputStream;
import java.nio.*;

import org.lwjgl.openal.*;

import de.jarnbjo.vorbis.VorbisAudioFileReader;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;

public class Sound {
	/** Buffers hold sound data. */
	IntBuffer buffer = BufferUtils.createIntBuffer(1);

	/** Sources are points emitting sound. */
	IntBuffer source = BufferUtils.createIntBuffer(1);

	/** Position of the source sound. */
	FloatBuffer sourcePos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f}).rewind();

	/** Velocity of the source sound. */
	FloatBuffer sourceVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f}).rewind();

	/** Position of the listener. */
	FloatBuffer listenerPos = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f})
			.rewind();

	/** Velocity of the listener. */
	FloatBuffer listenerVel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f})
			.rewind();

	/** Orientation of the listener. (first 3 elements are "at", second 3 are "up") */
	FloatBuffer listenerOri = (FloatBuffer) BufferUtils.createFloatBuffer(6)
			.put(new float[] {0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f}).rewind();

	/**
	 * boolean LoadALData()
	 * This function will load our sample data from the disk using the Alut
	 * utility and send the data into OpenAL as a buffer. A source is then
	 * also created to play that buffer.
	 */
	int loadALData() {
		// Load wav data into a buffer.
		AL10.alGenBuffers(buffer);

		if (AL10.alGetError() != AL10.AL_NO_ERROR) {
			return AL10.AL_FALSE;
		}

		//Loads the wave file from your file system
		InputStream fin;
		try {
			fin = Sound.class.getResourceAsStream("/sounds/soundtrackSmall.ogg");
		} catch(final Exception ex) {
			ex.printStackTrace();
			return AL10.AL_FALSE;
		}

		/*	    final WaveData waveFile = WaveData.create(fin);
			    try {
			      fin.close();
			    } catch (final java.io.IOException ex) {
			    }

				//Loads the wave file from this class's package in your classpath
				final WaveData waveFile = WaveData.create("FancyPants.wav");

				AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
				waveFile.dispose();
		*/
		VorbisAudioFileReader reader = new VorbisAudioFileReader();
		try {
			AudioInputStream stream = reader.getAudioInputStream(fin);
			int format;
			if (stream.getFormat().getChannels() > 1) {
				format = AL10.AL_FORMAT_STEREO16;
			} else {
				format = AL10.AL_FORMAT_MONO16;
			}

			int fileLength = (int) (stream.getFrameLength() / stream.getFormat().getFrameSize());
			ByteBuffer data = BufferUtils.createByteBuffer(fileLength);

			stream.read(data.array());

			AL10.alBufferData(buffer.get(0), format, data, (int) stream.getFormat().getSampleRate());
		} catch(IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}

		// Bind the buffer with the source.
		AL10.alGenSources(source);

		if (AL10.alGetError() != AL10.AL_NO_ERROR) {
			return AL10.AL_FALSE;
		}

		AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
		AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
		AL10.alSourcef(source.get(0), AL10.AL_GAIN, 1.0f);
		AL10.alSourcefv(source.get(0), AL10.AL_POSITION, sourcePos);
		AL10.alSourcefv(source.get(0), AL10.AL_VELOCITY, sourceVel);

		// Do another error check and return.
		if (AL10.alGetError() == AL10.AL_NO_ERROR) {
			return AL10.AL_TRUE;
		}

		return AL10.AL_FALSE;
	}

	/**
	 * void setListenerValues()
	 * We already defined certain values for the Listener, but we need
	 * to tell OpenAL to use that data. This function does just that.
	 */
	void setListenerValues() {
		AL10.alListenerfv(AL10.AL_POSITION, listenerPos);
		AL10.alListenerfv(AL10.AL_VELOCITY, listenerVel);
		AL10.alListenerfv(AL10.AL_ORIENTATION, listenerOri);
	}

	/**
	 * void killALData()
	 * We have allocated memory for our buffers and sources which needs
	 * to be returned to the system. This function frees that memory.
	 */
	void killALData() {
		AL10.alDeleteSources(source);
		AL10.alDeleteBuffers(buffer);
	}

	public static void main(final String[] args) {
		new Sound().execute();
	}

	public void execute() {
		// Initialize OpenAL and clear the error bit.
		long device = alcOpenDevice((ByteBuffer) null);
		ALCCapabilities deviceCapabilites = ALC.createCapabilities(device);
		long context = alcCreateContext(device, (IntBuffer) null);
		alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCapabilites);
		//alcOpenD
		AL10.alGetError();
		// Load the wav data.
		if (loadALData() == AL10.AL_FALSE) {
			System.out.println("Error loading data.");
			return;
		}

		setListenerValues();

		AL10.alSourcePlay(source.get(0));

		try {
			Thread.sleep(10000);
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AL10.alSourcePause(source.get(0));

		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AL10.alSourcePlay(source.get(0));

		try {
			Thread.sleep(10000);
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AL10.alSourceStop(source.get(0));

		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AL10.alSourcePlay(source.get(0));

		try {
			Thread.sleep(10000);
		} catch(InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		killALData();
		//TODO: FIND RIGHT CALL
	}
}
