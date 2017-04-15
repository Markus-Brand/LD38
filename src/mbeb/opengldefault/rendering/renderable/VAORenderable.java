package mbeb.opengldefault.rendering.renderable;

import static mbeb.opengldefault.constants.Constants.FLOAT_SIZE;
import static org.lwjgl.opengl.GL11.*;

import mbeb.opengldefault.gl.buffer.ElementBuffer;
import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gl.buffer.VertexBuffer;
import mbeb.opengldefault.gl.vao.VertexArray;
import org.joml.*;

import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.scene.*;

/**
 * Leaf Renderable - an actual OpenGL-VAO that can be rendered
 */
public class VAORenderable implements IRenderable {

	/** Class Name Tag */
	private static final String TAG = "Renderable";

	/** Renderables Vertex Array Object */
	private VertexArray VAO = null;

	private VertexBuffer VBO = null;
	private ElementBuffer EBO = null;

	/** amount of indices */
	private final int vertexCount;

	private final DataFragment[] dataFormat;
	/** the boundingBox of all my vertices */
	private BoundingBox boundingBox = new BoundingBox.Empty();
	/** the static mesh transformation */
	private Matrix4f transform;

	/**
	 * Constructor for Renderable with given Data
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
		this(indices.length, dataFormat);

		this.dataWriter().write(data).flush(GLBufferWriter.WriteType.FULL_DATA);
		this.indicesWriter().write(indices).flush(GLBufferWriter.WriteType.FULL_DATA);
		this.setAttribPointers();

		setBoundingBox(boundingBox);
	}

	/**
	 * Constructor for an empty Renderable: Use the DataWriter to add data, set a BoundingBox and don't forget to set the AttribPointers
	 * @param vertexCount
	 * @param format
	 */
	public VAORenderable(int vertexCount, DataFragment[] format) {
		this.vertexCount = vertexCount;
		this.dataFormat = format;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return Log.assertNotNull(TAG, boundingBox);
	}

	/**
	 * set a boundingBox for this Renderable.
	 * @param boundingBox
	 */
	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	@Override
	public Matrix4f getTransform() {
		if (transform == null) {
			transform = new Matrix4f();
		}
		return transform;
	}

	/**
	 * set a dedicated transformation for this mesh
	 * @param transform this meshes Transformation
	 */
	public void setTransform(Matrix4f transform) {
		this.transform = transform;
	}

	public VertexArray getVAO() {
		if (VAO == null) {
			VAO = new VertexArray();
		}
		return VAO;
	}

	/**
	 * get the ElementBuffer for this Renderable. If there was none, it will be created.
	 * If you never call this method, this Renderable will not render indexed.
	 * @return the ElementBuffer for this Renderable
	 */
	public ElementBuffer getEBO() {
		if (EBO == null) {
			bind();
			EBO = new ElementBuffer();
			EBO.bind();
			unbind();
			EBO.unbind();
		}
		return EBO;
	}

	/**
	 * get the VertexBuffer for this Renderable. If there was none, it will be created.
	 * @return the VertexBuffer for this Renderable
	 */
	public VertexBuffer getVBO() {
		if (VBO == null) {
			bind();
			VBO = new VertexBuffer();
			VBO.bind();
			unbind();
			VBO.unbind();
		}
		return VBO;
	}

	/**
	 * binds the Renderable
	 */
	public void bind() {
		getVAO().bind();
	}

	/**
	 * unbinds the Renderable
	 */
	public void unbind() {
		getVAO().unbind();
	}

	/**
	 * render the Renderable with a simple call to glDrawElements
	 *
	 * @param shader
	 */
	@Override
	public void render(ShaderProgram shader) {
		bind();
		if (EBO == null) {
			glDrawArrays(shader.getDrawMode().getGlEnum(), 0, vertexCount);
			GLErrors.checkForError(TAG, "glDrawArrays");
		} else {
			glDrawElements(shader.getDrawMode().getGlEnum(), vertexCount, GL_UNSIGNED_INT, 0);
			GLErrors.checkForError(TAG, "glDrawElements");
		}
		unbind();
	}

	/**
	 *
	 * @return a writer inside this renderables vbo
	 */
	public GLBufferWriter dataWriter() {
		return getVBO().writer(FLOAT_SIZE * vertexCount * DataFragment.getTotalSize(dataFormat))
				.setSpacingMode(false);
	}

	/**
	 * If you never call this one, this Renderable will use glDrawArrays instead of glDrawElements
	 * @return a writer inside this renderables index buffer.
	 */
	public GLBufferWriter indicesWriter() {
		return getEBO().writer(FLOAT_SIZE * vertexCount)
				.setSpacingMode(false);
	}

	/**
	 * Sets the attribute pointers for the vao according to the dataFormat.
	 * Call this once all data is inside the VBO.
	 */
	public void setAttribPointers() {
		bind();
		VBO.bind();
		getVAO().attribPointers(dataFormat);
		unbind();
		VBO.unbind();
	}
}
