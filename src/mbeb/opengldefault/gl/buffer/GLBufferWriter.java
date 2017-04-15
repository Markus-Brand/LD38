package mbeb.opengldefault.gl.buffer;

import java.awt.*;
import java.nio.ByteBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.logging.Log;

/**
 * An object that can be written on, which buffers multiple write calls to one buffer.
 */
public class GLBufferWriter {

	/**
	 * All the different write behaviours that a Writer can acquire
	 */
	public enum WriteType {
		/**
		 * definitely use bufferSubData
		 */
		SUB_DATA,
		/**
		 * use bufferSubData if (offset > 0 || writtenData < capacity)
		 */
		DYNAMIC,
		/**
		 * only use bufferSubData if an offset is specified
		 */
		FULL_DATA
	}

	private static final String TAG = "GLBufferWriter";

	/** the buffer to write to */
	private GLBuffer glBuffer;
	/** the offset to write at */
	private long offset;
	/** local cache to store data in, before writing everything to the buffer */
	private ByteBuffer writeBuffer;
	/** whether to write data according to the std140 - layout (instead of packed) */
	private boolean useSpacing;

	GLBufferWriter(GLBuffer glBuffer, long offset, int capacity) {
		this.glBuffer = glBuffer;
		this.offset = offset;

		writeBuffer = BufferUtils.createByteBuffer(capacity);
		useSpacing = true;
	}

	//<editor-fold desc="write">

	/**
	 * write a single int to the buffer
	 * 
	 * @param value
	 *            the value to write
	 * @return this, for chaining
	 */
	public GLBufferWriter write(int value) {
		writeBuffer.putInt(value);
		return this;
	}

	/**
	 * write multiple ints to the buffer
	 *
	 * @param data
	 *            the int-array to write
	 * @return this, for chaining
	 */
	public GLBufferWriter write(int[] data) {
		for (int datum : data) {
			write(datum);
		}
		return this;
	}

	/**
	 * write a single float to the buffer
	 *
	 * @param value
	 *            the value to write
	 * @return this, for chaining
	 */
	public GLBufferWriter write(float value) {
		writeBuffer.putFloat(value);
		return this;
	}

	/**
	 * write multiple floats to the buffer
	 *
	 * @param data
	 *            the float-array to write
	 * @return this, for chaining
	 */

	public GLBufferWriter write(float[] data) {
		for (float datum : data) {
			write(datum);
		}
		return this;
	}

	/**
	 * write a Matrix4f to the buffer
	 * This method fills up some space if required for the layout.
	 *
	 * @param value
	 *            the value to write
	 * @return this, for chaining
	 */
	public GLBufferWriter write(Matrix4f value) {
		if (useSpacing) {
			makeSpaceFor(16);
			value.get(writeBuffer);
			writeBuffer.position(writeBuffer.position() + Constants.MAT4_SIZE);
		} else {
			write(value.get(new float[16]));
		}
		return this;
	}

	/**
	 * write a Vector2f to the buffer
	 * This method fills up some space if required for the layout.
	 *
	 * @param value
	 *            the value to write
	 * @return this, for chaining
	 */
	public GLBufferWriter write(Vector2f value) {
		if (useSpacing) {
			makeSpaceFor(2);
			value.get(writeBuffer);
			writeBuffer.position(writeBuffer.position() + Constants.VEC2_SIZE);
		} else {
			write(value.x());
			write(value.y());
		}
		return this;
	}

	/**
	 * write a Vector3f to the buffer
	 * This method fills up some space if required for the layout.
	 *
	 * @param value
	 *            the value to write
	 * @return this, for chaining
	 */
	public GLBufferWriter write(Vector3f value) {
		if (useSpacing) {
			makeSpaceFor(3);
			value.get(writeBuffer);
			writeBuffer.position(writeBuffer.position() + Constants.VEC3_SIZE);
		} else {
			write(value.x());
			write(value.y());
			write(value.z());
		}
		return this;
	}

	/**
	 * write a Vector4f to the buffer
	 * This method fills up some space if required for the layout.
	 *
	 * @param value
	 *            the value to write
	 * @return this, for chaining
	 */
	public GLBufferWriter write(Vector4f value) {
		if (useSpacing) {
			makeSpaceFor(4);
			value.get(writeBuffer);
			writeBuffer.position(writeBuffer.position() + Constants.VEC4_SIZE);
		} else {
			write(value.x());
			write(value.y());
			write(value.z());
			write(value.w());
		}
		return this;
	}

	/**
	 * write the values of a color as 3 or 4 floats (in range 0 to 1)
	 * 
	 * @param color
	 *            the Color to write
	 * @param includeAlpha
	 *            true to use a vec4, false to use a vec3
	 * @return this, for chaining
	 * @see #write(Vector3f)
	 * @see #write(Vector4f)
	 */
	public GLBufferWriter write(Color color, boolean includeAlpha) {
		if (includeAlpha) {
			write(new Vector4f(color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f, color.getAlpha() / 256f));
		} else {
			write(new Vector3f(color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f));
		}
		return this;
	}

	/**
	 * write a (maybe complex) object to the buffer,
	 * by calling its {@link GLBufferWritable#writeTo(GLBufferWriter)} - method
	 * 
	 * @param value
	 *            the object to write to this buffer
	 * @return this, for chaining
	 */
	public GLBufferWriter write(GLBufferWritable value) {
		value.writeTo(this);
		return this;
	}
	//</editor-fold>

	//<editor-fold desc="spacing">
	/**
	 * fill or not, depending on whether a struct with the given primitive-amount still fits inside the buffer.
	 * Does nothing if {@link #useSpacing} == false.
	 * 
	 * @param floatCount
	 *            the number of floats that want to have space
	 */
	private void makeSpaceFor(int floatCount) {
		if (freeFloats() < floatCount) {
			fillBlock();
		}
	}

	/**
	 * @return how many floats are already in the last opened block
	 */
	private int latestFloatCount() {
		int floatCount = writeBuffer.position() / Constants.FLOAT_SIZE;
		return floatCount % Constants.FLOATS_PER_BLOCK; //floats per block
	}

	/**
	 * @return the number of floats that fit into the latest block, in interval [0, FloatsPerBlock-1]
	 */
	private int freeFloats() {
		int latestFloats = latestFloatCount();
		return latestFloats == 0 ? 0 : Constants.FLOATS_PER_BLOCK - latestFloats;
	}

	/**
	 * fills up the byteBuffer with zero-floats until the next block is reached.
	 * Does nothing if {@link #useSpacing} == false.
	 * 
	 * @return this, for chaining
	 */
	public GLBufferWriter fillBlock() {
		if (useSpacing) {
			fill(freeFloats());
		}
		return this;
	}

	/**
	 * fills some empty floats into this buffer.
	 * 
	 * @param floatCount
	 *            the amount of floats to fill
	 */
	private void fill(int floatCount) {
		for (int f = 0; f < floatCount; f++) {
			writeBuffer.putFloat(0f);
		}
	}
	//</editor-fold>

	/**
	 * enable / disable the spacing mode for this buffer-writer (it is enabled by default)
	 * if spacing is enabled, this writer aligns newly written objects to the blocks defined in the std140 - layout
	 * if not enabled, this writer adds new data right after previous data
	 *
	 * @param useSpacing
	 *            whether to write in spacing mode or not
	 * @return this, for chaining
	 */
	public GLBufferWriter setSpacingMode(boolean useSpacing) {
		this.useSpacing = useSpacing;
		return this;
	}

	/**
	 * finish adding data to this Writer and upload all that's been written to the GPU
	 */
	public void flush() {
		flush(true, true);
	}

	/**
	 * finish adding data to this Writer and upload all that's been written to the GPU
	 * 
	 * @param shouldBindBefore
	 *            whether to call bind on the GLBuffer-object before the flush
	 * @param shouldUnbindAfter
	 *            whether to call unbind on the GLBuffer-object after the flush
	 */
	public void flush(boolean shouldBindBefore, boolean shouldUnbindAfter) {
		flush(shouldBindBefore, shouldUnbindAfter, WriteType.DYNAMIC);
	}

	/**
	 * finish adding data to this Writer and upload all that's been written to the GPU
	 *
	 * @param writeType
	 *            the behaviour that determines which method to use to write the data
	 */
	public void flush(WriteType writeType) {
		flush(true, true, writeType);
	}

	/**
	 * finish adding data to this Writer and upload all that's been written to the GPU
	 * 
	 * @param shouldBindBefore
	 *            whether to call bind on the GLBuffer-object before the flush
	 * @param shouldUnbindAfter
	 *            whether to call unbind on the GLBuffer-object after the flush
	 * @param writeType
	 *            the behaviour that determines which method to use to write the data
	 */
	public void flush(boolean shouldBindBefore, boolean shouldUnbindAfter, WriteType writeType) {
		if (shouldBindBefore) {
			glBuffer.bind();
		}
		if (offset > 0 || writeType == WriteType.SUB_DATA || (writeType == WriteType.DYNAMIC && writeBuffer.position() < writeBuffer.capacity())) {
			glBuffer.bufferSubData(offset, writeBuffer);
		} else {
			glBuffer.bufferData(writeBuffer, GLBuffer.Usage.STATIC_DRAW);
		}
		if (shouldUnbindAfter) {
			glBuffer.unbind();
		}
		glBuffer = null; //prevent further writing with this object
	}

	@Override
	protected void finalize() throws Throwable {
		if (glBuffer != null) {
			Log.error(TAG, "You forgot to flush a GLBufferWriter!");
		}
		super.finalize();
	}
}
