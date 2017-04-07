package mbeb.opengldefault.rendering.renderable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.*;

import mbeb.opengldefault.gl.buffer.ElementBuffer;
import mbeb.opengldefault.gl.buffer.VertexBuffer;
import org.joml.*;
import org.lwjgl.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

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
	/** the static mesh transformation */
	private Matrix4f transform;

	/**
	 * Constructor for Renderable
	 *
	 * @param data
	 *            vertex data. Contains vertex position, texture coordinates, normals, color and maybe other data
	 * @param indices
	 *            index data. The order in which the vertex data is read
	 * @param dataFormat
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 * @param boundingBox
	 *            the bounding box of the vertex data
	 */
	public VAORenderable(float[] data, int[] indices, DataFragment[] dataFormat, BoundingBox boundingBox) {
		this.indexSize = indices.length;
		this.boundingBox = boundingBox;
		this.VAO = generateVAO(data, indices, dataFormat);
	}

	/**
	 * Constructor for Renderable
	 *
	 * @param vertexBuffer
	 *            vertex data in a FloatBuffer. Contains vertex position, texture coordinates, normals, color and maybe
	 *            other data
	 * @param indexBuffer
	 *            index data in a IntBuffer. The order in which the vertex data is read
	 * @param dataFormat
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 */
	public VAORenderable(FloatBuffer vertexBuffer, IntBuffer indexBuffer, DataFragment[] dataFormat, BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
		this.indexSize = indexBuffer.capacity();
		this.VAO = generateVAO(vertexBuffer, indexBuffer, dataFormat);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	@Override
	public Matrix4f getTransform() {
		if (transform == null) {
			transform = new Matrix4f();
		}
		return transform;
	}

	/**
	 * this meshes Transformation
	 * 
	 * @param transform
	 */
	public void setTransform(Matrix4f transform) {
		this.transform = transform;
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
	 * unbinds the Renderable
	 */
	public void unbind() {
		glBindVertexArray(0);
		GLErrors.checkForError(TAG, "glBindVertexArray");
	}

	/**
	 * render the Renderable with a simple call to glDrawElements
	 *
	 * @param shader
	 */
	@Override
	public void render(ShaderProgram shader) {
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
	 * @param dataFormat
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 * @return generated VAO
	 */
	public static int generateVAO(float[] data, int[] indices, DataFragment[] dataFormat) {

		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(data.length);

		vertexBuffer.put(data);

		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);

		indexBuffer.put(indices);

		return generateVAO(vertexBuffer, indexBuffer, dataFormat);
	}

	/**
	 * Static method for generating a VAO
	 *
	 * @param vertexBuffer
	 *            vertex data in a FloatBuffer. Contains vertex position, texture coordinates, normals, color and maybe
	 *            other data
	 * @param indexBuffer
	 *            index data in a IntBuffer. The order in which the vertex data is read
	 * @param dataFormat
	 *            size of the components in the data array in amount of floats. a RGB color would be represented by a 3
	 * @return generated VAO
	 */
	public static int generateVAO(FloatBuffer vertexBuffer, IntBuffer indexBuffer, DataFragment[] dataFormat) {
		int VAO = glGenVertexArrays();
		GLErrors.checkForError(TAG, "glGenVertexArrays");
		glBindVertexArray(VAO);
		GLErrors.checkForError(TAG, "glBindVertexArray");

		VertexBuffer VBO = generateVBO(vertexBuffer, dataFormat);

		ElementBuffer EBO = generateEBO(indexBuffer);
		GLErrors.checkForError(TAG, "generateEBO");

		glBindVertexArray(0);
		GLErrors.checkForError(TAG, "glBindVertexArray");

		EBO.delete();
		VBO.delete();

		return VAO;
	}

	/**
	 * Static method for generating a EBO
	 *
	 * @param indexBuffer
	 *            index data in a IntBuffer. The order in which the vertex data is read
	 * @return generated EBO
	 */
	private static ElementBuffer generateEBO(IntBuffer indexBuffer) {
		ElementBuffer EBO = new ElementBuffer();
		EBO.bind();
		EBO.bufferData(indexBuffer, GL_STATIC_DRAW);
		return EBO;
	}

	/**
	 * Static method for generating a VBO
	 *
	 * @param vertexBuffer
	 *            vertex data in a FloatBuffer. Contains vertex position, texture coordinates, normals, color and maybe
	 *            other data
	 * @param dataFormat
	 *            DataFragments that describe how the data is stored in the buffer.
	 * @return generated VBO
	 */
	private static VertexBuffer generateVBO(FloatBuffer vertexBuffer, DataFragment[] dataFormat) {
		VertexBuffer VBO = new VertexBuffer();
		VBO.bind();

		VBO.bufferData(vertexBuffer, GL_STATIC_DRAW);

		int stride = 0;
		for (DataFragment dataFragemt : dataFormat) {
			stride += 4 * dataFragemt.size(); //size of float
		}

		int offset = 0;
		for (int i = 0; i < dataFormat.length; i++) {
			if (dataFormat[i].isFloat()) {
				glVertexAttribPointer(i, dataFormat[i].size(), GL_FLOAT, false, stride, offset);
			} else {
				glVertexAttribIPointer(i, dataFormat[i].size(), GL_INT, stride, offset);
			}
			GLErrors.checkForError(TAG, "glVertexAttribPointer VBO");
			glEnableVertexAttribArray(i);
			GLErrors.checkForError(TAG, "glEnableVertexAttribArray VBO");
			offset += dataFormat[i].size() * 4;
		}
		return VBO;
	}
}
