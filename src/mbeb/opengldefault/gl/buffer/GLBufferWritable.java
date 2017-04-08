package mbeb.opengldefault.gl.buffer;

/**
 * Things that can write themselves to a GLBufferWriter
 */
public interface GLBufferWritable {
	void writeTo(GLBufferWriter writer);
}
