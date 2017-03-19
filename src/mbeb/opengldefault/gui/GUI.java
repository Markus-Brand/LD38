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

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.StaticMeshes;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * A GUI that contains {@link GUIElement}s that get rendered in one drawcall
 *
 * @author Markus
 */
public class GUI implements IRenderable {
	private static final String TAG = "GUI";

	protected static final int FLOAT_SIZE = 4;
	protected static final int VEC4_SIZE = FLOAT_SIZE * 4;
	protected static final int MAT4_SIZE = VEC4_SIZE * 4;

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
		this.stride = MAT4_SIZE;
		renderable = StaticMeshes.getNewGuiQuad();
	}

	/**
	 * Adds a new {@link GUIElement}
	 *
	 * @param element
	 */
	public void addGUIElement(GUIElement element) {
		elements.add(element);
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
		int bufferSize = stride / FLOAT_SIZE;
		FloatBuffer buffer = BufferUtils.createFloatBuffer(getElementsSize() * bufferSize);
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
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(3, 4, GL_FLOAT, false, stride, 0 * VEC4_SIZE);
		glEnableVertexAttribArray(4);
		glVertexAttribPointer(4, 4, GL_FLOAT, false, stride, 1 * VEC4_SIZE);
		glEnableVertexAttribArray(5);
		glVertexAttribPointer(5, 4, GL_FLOAT, false, stride, 2 * VEC4_SIZE);
		glEnableVertexAttribArray(6);
		glVertexAttribPointer(6, 4, GL_FLOAT, false, stride, 3 * VEC4_SIZE);
		GLErrors.checkForError(TAG, "glVertexAttribPointer");

		glVertexAttribDivisor(3, 1);
		glVertexAttribDivisor(4, 1);
		glVertexAttribDivisor(5, 1);
		glVertexAttribDivisor(6, 1);
		GLErrors.checkForError(TAG, "glVertexAttribDivisor");
		renderable.unbind();
	}

	@Override
	public void render(Shader shader) {
		glBindBuffer(GL_ARRAY_BUFFER, buffer);
		if (dirty) {
			setupVAO();
			loadBufferData();
			dirty = false;
		}

		glDepthFunc(GL_LEQUAL);
		GLErrors.checkForError(TAG, "glDepthFunc");
		renderable.bind();
		glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, getElementsSize());
		GLErrors.checkForError(TAG, "glDrawElementsInstanced");
		renderable.unbind();
		glDepthFunc(GL_LESS);
		GLErrors.checkForError(TAG, "glDepthFunc");
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		GLErrors.checkForError(TAG, "glBindBuffer");
	}

	public int getElementsSize() {
		int numElements = 0;
		for (GUIElement element : elements) {
			if (element instanceof CombinedGUIElement) {
				numElements += ((CombinedGUIElement) element).getElements().size();
			} else {
				numElements++;
			}
		}
		return numElements;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox.Empty();
	}

	@Override
	public void update(double deltaTime) {
		for (GUIElement guiElement : elements) {
			guiElement.update(deltaTime);
			dirty = dirty || guiElement.isDirty();
			guiElement.setClean();
		}
	}

}
