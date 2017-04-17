package mbeb.opengldefault.gl.buffer;

/**
 * Things that can write themselves to a GLBufferWriter
 */
public interface GLBufferWritable {
	/**
	 * Write this object to a writer
	 * 
	 * @param writer
	 *            the GLBufferWriter to write to
	 */
	void writeTo(GLBufferWriter writer);
}
