package mbeb.opengldefault.rendering.renderable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.scene.BoundingBox;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Leaf Renderable - an actual OpenGL-VAO that can be rendered
 */
public class VAORenderable implements IRenderable {

	/** Class Name Tag */
	private static final String TAG = "Renderable";

	/** Renderables Vertex Array Object */
	private int VAO;
	/** amount of indices */
	private int indexSize;
	/** the boundingBox of all my vertices */
	private final BoundingBox boundingBox;

	/**
	 * Constructor for Renderable
	 *
	 * @param data
	 *            vertex data. Contains vertex position, texture coordinates, normals, color and maybe other data
	 * @param indices
	 *            index data. The order in which the vertex data is read
	 * @param dataSizes
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 * @param boundingBox
	 *            the bounding box of the vertex data
	 */
	public VAORenderable(float[] data, int[] indices, int[] dataSizes, BoundingBox boundingBox) {
		this.indexSize = indices.length;
		this.boundingBox = boundingBox;
		this.VAO = generateVAO(data, indices, dataSizes);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	/**
	 * Constructor for Renderable
	 *
	 * @param vertexBuffer
	 *            vertex data in a FloatBuffer. Contains vertex position, texture coordinates, normals, color and maybe other data
	 * @param indexBuffer
	 *            index data in a IntBuffer. The order in which the vertex data is read
	 * @param dataSizes
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 */
	public VAORenderable(FloatBuffer vertexBuffer, IntBuffer indexBuffer, int[] dataSizes, BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
		this.indexSize = indexBuffer.capacity();
		this.VAO = generateVAO(vertexBuffer, indexBuffer, dataSizes);
	}

	/**
	 * sets VAO
	 *
	 * @param VAO
	 *            new VAO
	 */
	public void setVAO(int VAO) {
		this.VAO = VAO;
	}

	/**
	 * get VAO
	 *
	 * @return VAO
	 */
	public int getVAO() {
		return VAO;
	}

	/**
	 * binds the Renderable
	 */
	public void bind() {
		glBindVertexArray(VAO);
		GLErrors.checkForError(TAG, "glBindVertexArray");
	}

	/**
	 * unbind the Renderable
	 */
	public void unbind() {
		glBindVertexArray(0);
	}

	/**
	 * render the Renderable with a simple call to glDrawElements
	 *
	 * @param shader
	 */
	@Override
	public void render(Shader shader) {
		bind();
		glDrawElements(shader.getDrawMode(), indexSize, GL_UNSIGNED_INT, 0);
		GLErrors.checkForError(TAG, "glDrawElements");
		unbind();
	}

	/**
	 * Static method for generating a VAO
	 *
	 * @param data
	 *            vertex data. Contains vertex position, texture coordinates, normals, color and maybe other data
	 * @param indices
	 *            index data. The order in which the vertex data is read
	 * @param dataSizes
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 * @return generated VAO
	 */
	public static int generateVAO(float[] data, int[] indices, int[] dataSizes) {

		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(data.length);

		vertexBuffer.put(data);

		vertexBuffer.flip();

		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);

		indexBuffer.put(indices);

		indexBuffer.flip();

		return generateVAO(vertexBuffer, indexBuffer, dataSizes);
	}

	/**
	 * Static method for generating a VAO
	 *
	 * @param vertexBuffer
	 *            vertex data in a FloatBuffer. Contains vertex position, texture coordinates, normals, color and maybe other data
	 * @param indexBuffer
	 *            index data in a IntBuffer. The order in which the vertex data is read
	 * @param dataSizes
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 * @return generated VAO
	 */
	public static int generateVAO(FloatBuffer vertexBuffer, IntBuffer indexBuffer, int[] dataSizes) {
		int VAO = glGenVertexArrays();
		GLErrors.checkForError(TAG, "glGenVertexArrays");
		glBindVertexArray(VAO);
		GLErrors.checkForError(TAG, "glBindVertexArray");

		int VBO = generateVBO(vertexBuffer, dataSizes);

		int EBO = generateEBO(indexBuffer);
		GLErrors.checkForError(TAG, "generateEBO");

		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		glDeleteBuffers(EBO);
		GLErrors.checkForError(TAG, "glDeleteBuffers EBO");
		glDeleteBuffers(VBO);
		GLErrors.checkForError(TAG, "glDeleteBuffers VBO");

		return VAO;
	}

	/**
	 * Static method for generating a EBO
	 *
	 * @param indexBuffer
	 *            index data in a IntBuffer. The order in which the vertex data is read
	 * @return generated EBO
	 */
	private static int generateEBO(IntBuffer indexBuffer) {
		int EBO;
		EBO = glGenBuffers();
		GLErrors.checkForError(TAG, "glGenBuffers EBO");
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		GLErrors.checkForError(TAG, "glBindBuffer EBO");
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		GLErrors.checkForError(TAG, "glBufferData EBO");
		return EBO;
	}

	/**
	 * Static method for generating a VBO
	 *
	 * @param vertexBuffer
	 *            vertex data in a FloatBuffer. Contains vertex position, texture coordinates, normals, color and maybe other data
	 * @param dataSizes
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 * @return generated VBO
	 */
	private static int generateVBO(FloatBuffer vertexBuffer, int[] dataSizes) {
		int VBO;
		VBO = glGenBuffers();
		GLErrors.checkForError(TAG, "glGenBuffers VBO");

		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		GLErrors.checkForError(TAG, "glBindBuffer VBO");
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		GLErrors.checkForError(TAG, "glBufferData VBO");

		int stride = 0;
		for (int dataSize : dataSizes) {
			stride += 4 * dataSize;
		}

		int offset = 0;
		for (int i = 0; i < dataSizes.length; i++) {
			glVertexAttribPointer(i, dataSizes[i], GL_FLOAT, false, stride, offset);
			GLErrors.checkForError(TAG, "glVertexAttribPointer VBO");
			glEnableVertexAttribArray(i);
			GLErrors.checkForError(TAG, "glEnableVertexAttribArray VBO");
			offset += dataSizes[i] * 4;
		}
		return VBO;
	}
}
