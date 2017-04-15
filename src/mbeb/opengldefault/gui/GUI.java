package mbeb.opengldefault.gui;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;

import java.nio.*;

import org.lwjgl.BufferUtils;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gui.elements.GUIElement;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.StaticMeshes;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import mbeb.opengldefault.rendering.shader.ShaderProgram;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * A GUI that contains {@link GUIElement}s that get rendered in one drawcall
 *
 * @author Markus
 */
public class GUI implements IRenderable {
	private static final String TAG = "GUI";

	/**
	 * The look up table Texture for this GUI
	 */
	private Texture lut;

	/**
	 * The shader used to render this GUI
	 */
	private ShaderProgram shader;

	/**
	 * GUIElements that get drawn with this GUI
	 */
	protected List<GUIElement> elements;

	/**
	 * basic renderable containing a simple gui quad and gui specific instanced vertex data
	 *
	 * @see StaticMeshes
	 */
	protected VAORenderable renderable;

	/**
	 * gui specific instanced vertex data buffer handle
	 */
	protected int buffer;

	/**
	 * dirty flag
	 * true if data has changed and the buffer has to be updated
	 */
	private boolean dirty;

	/**
	 * Vertex Data Stride: MAT4_SIZE + size of additional gui data
	 */
	protected int stride;

	public GUI() {
		elements = new ArrayList<>();
		dirty = true;
		setupBuffer();
		//Store a Matrix and the lut Vector
		this.stride = Constants.MAT4_COMPONENTS + Constants.VEC4_COMPONENTS;
		renderable = StaticMeshes.getNewGuiQuad();
		lut = new Texture(256, 256);
	}

	/**
	 * Adds a new GUIElement and sets its lut level
	 * 
	 * @param newElement
	 *            the new element
	 * @return the new element
	 */
	public GUIElement addGUIElement(GUIElement newElement) {
		newElement.setLut(getLut(), elements.size() % 256 / 255f);
		elements.add(newElement);
		return newElement;
	}

	/**
	 * Generates a new buffer
	 */
	private void setupBuffer() {
		buffer = glGenBuffers();
	}

	/**
	 * Buffers data from the gui elements into the {@link #buffer}
	 */
	private void loadBufferData() {
		glBufferData(GL_ARRAY_BUFFER, getFloatBuffer(), GL_STATIC_DRAW);
		GLErrors.checkForError(TAG, "glBufferData");
	}

	/**
	 * Generates a FloatBuffer using the GUIElements {@link GUIElement#writeToBuffer()}
	 *
	 * @return the generated FloatBuffer
	 */
	private FloatBuffer getFloatBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(getElementsSize() * stride);
		int offset = 0;
		for (GUIElement guiElement : elements) {
			offset += guiElement.writeToBuffer(buffer, offset);
		}
		return buffer;
	}

	/**
	 * Setup Vertex Attributes.
	 * Per default this only contains data for the model matrix
	 */
	public void setupVAO() {
		renderable.bind();
		for (int i = 0; i < stride / Constants.VEC4_COMPONENTS; i++) {
			/* The first two Vertex Attributes are reserved for position and texCoordinates*/
			int vertexAttribArrayIndex = i + 2;
			glEnableVertexAttribArray(vertexAttribArrayIndex);
			GLErrors.checkForError(TAG, "glEnableVertexAttribArray");
			glVertexAttribPointer(vertexAttribArrayIndex, 4, GL_FLOAT, false, stride * Constants.FLOAT_SIZE, i
					* Constants.VEC4_SIZE);
			GLErrors.checkForError(TAG, "glVertexAttribPointer");

			glVertexAttribDivisor(vertexAttribArrayIndex, 1);
			GLErrors.checkForError(TAG, "glVertexAttribDivisor");
		}
		renderable.unbind();
	}

	public void render() {
		shader.use();
		render(shader);
	}

	@Override
	public void render(ShaderProgram shader) {
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
		if (dirty) {
			setupVAO();
			loadBufferData();
			dirty = false;
		}

		if (lut != null) {
			lut.bind(shader, "u_lut");
		}

		glEnable(GL_BLEND);
		glDisable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GLErrors.checkForError(TAG, "glDepthFunc");
		renderable.bind();
		glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, getElementsSize());
		GLErrors.checkForError(TAG, "glDrawElementsInstanced");
		renderable.unbind();
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		GLErrors.checkForError(TAG, "glDepthFunc");
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GLErrors.checkForError(TAG, "glBindBuffer");
	}

	public int getElementsSize() {
		return elements.stream().mapToInt(element -> element.getNumElements()).sum();
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox.Empty();
	}

	@Override
	public void update(double deltaTime) {
		for (GUIElement guiElement : elements) {
			guiElement.update(deltaTime);
			dirty |= guiElement.isDirty();
		}
	}

	/**
	 * Getter for the lut
	 *
	 * @return this GUIs lut
	 */
	public Texture getLut() {
		return lut;
	}

	/**
	 * Getter for the Shader
	 *
	 * @return the GUIs Shader
	 */
	public ShaderProgram getShader() {
		return shader;
	}

	/**
	 * @param shader
	 *            the shader to set
	 */
	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}
}
