package mbeb.opengldefault.gui;

import static mbeb.opengldefault.constants.Constants.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

import java.util.ArrayList;
import java.util.List;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gl.buffer.VertexBuffer;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.gl.vao.VertexArray;
import mbeb.opengldefault.gui.elements.GUIElement;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.StaticMeshes;
import mbeb.opengldefault.rendering.renderable.VAORenderable;
import mbeb.opengldefault.scene.BoundingBox;

/**
 * A GUI that contains {@link GUIElement}s that get rendered in one drawcall
 *
 * @author Markus
 */
public class GUI implements IRenderable {
	private static final String TAG = "GUI";

	/**
	 * Loads a texture and sets its properties to make it suitable for use in GUI rendering.
	 * 
	 * @param path
	 *            the path of the texture to load
	 * @return the loaded texture
	 */
	public static Texture2D loadGUITexture(String path) {
		Texture2D loaded = new Texture2D(path);
		setGUIParameters(loaded);
		return loaded;
	}

	/**
	 * Sets a textures properties to make it suitable for GUI rendering.
	 * 
	 * @param texture
	 *            the texture to setup
	 * @return whether the operation succeeded
	 */
	public static boolean setGUIParameters(Texture texture) {
		return texture.whileBound(glObject -> texture.setWrapMode(Texture.WrapMode.CLAMP_TO_EDGE) && texture.setInterpolates(false));
	}

	/**
	 * The look up table Texture for this GUI
	 */
	private Texture2D lut;

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
	protected VertexBuffer vbo;

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
		this.stride = MAT4_COMPONENTS + VEC4_COMPONENTS;
		renderable = StaticMeshes.getNewGuiQuad();
		lut = new Texture2D(256, 256, mbeb.opengldefault.gl.texture.Texture.InternalFormat.RGBA8);
		setGUIParameters(lut);
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
		vbo = new VertexBuffer();
		vbo.ensureExists();
	}

	/**
	 * Buffers data from the gui elements into the {@link #vbo}
	 */
	private void loadBufferData() {
		GLBufferWriter writer = vbo.writer(getElementsSize() * stride * FLOAT_SIZE);
		elements.forEach(writer::write);
		writer.flush(GLBufferWriter.WriteType.FULL_DATA);
	}

	/**
	 * Setup Vertex Attributes.
	 * Per default this only contains data for the model matrix
	 */
	public void setupVAO() {
		VertexArray VAO = renderable.getVAO();
		VAO.bind();
		VAO.trimPointers(2);
		for (int i = 0; i < stride / VEC4_COMPONENTS; i++) {
			VAO.attribPointer(FLOAT_SIZE, VertexArray.AttributePointer.Type.FLOAT, false, stride * FLOAT_SIZE, i * VEC4_SIZE);

			VAO.getLastPointer().instanced();
		}
		VAO.unbind();
	}

	public void render() {
		shader.use();
		render(shader);
	}

	@Override
	public void render(ShaderProgram shader) {
		vbo.bind();
		if (dirty) {
			setupVAO();
			loadBufferData();
			dirty = false;
		}

		if (lut != null) {
			this.lut.bind();
			shader.setUniform("u_lut", lut, false, true);
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
		vbo.unbind();
	}

	public int getElementsSize() {
		return elements.stream().mapToInt(GUIElement::getNumElements).sum();
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
	public Texture2D getLut() {
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
