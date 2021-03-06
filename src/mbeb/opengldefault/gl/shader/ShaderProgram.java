package mbeb.opengldefault.gl.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.gl.buffer.UniformBuffer;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;
import mbeb.opengldefault.gl.ContextBindings;

/**
 * Shader Object used for rendering a {@link mbeb.opengldefault.rendering.renderable.IRenderable}
 *
 * @author Markus, Erik
 */
public class ShaderProgram extends GLObject {

	private static final String TAG = "ShaderProgram";

	/**
	 * The draw mode of an OpenGL shader program
	 */
	public enum DrawMode {
		POINTS(GL_POINTS), LINES(GL_LINES), TRIANGLES(GL_TRIANGLES);

		private int glEnum;

		DrawMode(int glEnum) {
			this.glEnum = glEnum;
		}

		/**
		 * @return the OpenGL enum for this draw mode
		 */
		public int getGlEnum() {
			return glEnum;
		}
	}

	/** Drawmode for the Renderables that get rendered by this shader */
	private DrawMode drawMode = DrawMode.TRIANGLES;
	/** the shader components */
	private Map<ShaderObjectType, ShaderObject> shaderObjects;

	private boolean compiled = false;

	/**
	 * handles current parameter set and offers precompilation of Shader-Files
	 */
	private final ShaderPreprocessor preprocessor;

	/**
	 * Uniform Blocks used in the shader. Maps from the name of the uniform block to the index of the uniform block.
	 */
	private final Map<String, Integer> uniformBlocks;

	/**
	 * constructor of a shader object.
	 *
	 * @param shaders
	 *            the paths to all shader objects
	 */
	public ShaderProgram(final String ... shaders) {
		this(new HashMap<>(), shaders);
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param parameters
	 *            a map containing initial values for shader parameters
	 * @param shaders
	 *            the paths to all shader objects
	 */
	public ShaderProgram(final Map<String, String> parameters, final String ... shaders) {
		this.preprocessor = new ShaderPreprocessor(parameters);
		uniformBlocks = new HashMap<>();

		shaderObjects = new EnumMap<>(ShaderObjectType.class);
		for (String path : shaders) {
			ShaderObject obj = new ShaderObject(path, preprocessor);
			shaderObjects.put(obj.getType(), obj);
		}

		Log.assertTrue(TAG, shaderObjects.containsKey(ShaderObjectType.VERTEX), "Vertex shader required");
		Log.assertTrue(TAG, shaderObjects.containsKey(ShaderObjectType.FRAGMENT), "Fragment shader required");
		Log.assertEqual(TAG, shaderObjects.containsKey(ShaderObjectType.TCS), shaderObjects.containsKey(ShaderObjectType.TES), "TCS and TES are only allowed together.");
	}

	@Override
	protected Integer glGenerate() {
		int handle = glCreateProgram();
		if (handle == 0 || GLErrors.checkForError(TAG, "glCreateProgram")) {
			return null;
		}
		return handle;
	}

	@Override
	protected boolean glBind() {
		ContextBindings.bind(this);
		glUseProgram(this.getHandle());
		boolean success = !GLErrors.checkForError(TAG, "glUseProgram");
		if (!success) {
			ContextBindings.unbindShader();
		}
		return success;
	}

	@Override
	protected boolean isBoundToContext() {
		return ContextBindings.isBound(this);
	}

	@Override
	protected boolean glUnbind() {
		ContextBindings.unbindShader();
		glUseProgram(0);
		return !GLErrors.checkForError(TAG, "glUseProgram");
	}

	@Override
	protected boolean glDelete() {
		glDeleteProgram(this.getHandle());
		return !GLErrors.checkForError(TAG, "glDeleteProgram");
	}

	/**
	 * Adds a Uniform Buffer Block to the Shader
	 *
	 * @param UBO
	 *            the UBO to add
	 */
	public void addUniformBlockIndex(final UniformBuffer UBO) {
		this.addUniformBlockIndex(UBO.getBaseName(), UBO.getBaseIndex());
	}

	/**
	 * Adds a name index combination of a uniform buffer to the shader.
	 * @param name the name of the ubo to add
	 * @param index the index of the ubo to add
	 */
	public void addUniformBlockIndex(final String name, final int index) {
		uniformBlocks.put(name, index);
		this.setUniformBlockIndex(name, index);
	}

	/**
	 * Registers the given UBO index with the shader under the given name.
	 * @param name the name of the ubo
	 * @param index the index of the ubo
	 */
	private void setUniformBlockIndex(final String name, final int index) {
		this.ensureCompiled();
		final int uniformBlockIndex = glGetUniformBlockIndex(this.getHandle(), name);
		GLErrors.checkForError(TAG, "glGetUniformBlockIndex");

		glUniformBlockBinding(this.getHandle(), uniformBlockIndex, index);
		GLErrors.checkForError(TAG, "glUniformBlockBinding");
	}

	/**
	 * use Shader
	 */
	public void use() {
		ensureUpToDate();
		this.bind();
	}

	/**
	 * get the location of an Uniform with given name
	 *
	 * @param name
	 *            name of the uniform
	 * @return the uniforms location
	 */
	public int getUniform(final String name) {
		return getUniform(name, true);
	}

	/**
	 * get the location of a Uniform with given name
	 *
	 * @param name
	 *            name of the uniform
	 * @param logAnError
	 *            log an error if not found
	 * @return the uniforms location
	 */
	public int getUniform(final String name, final boolean logAnError) {
		ensureCompiled();
		final int uniformLocation = glGetUniformLocation(this.getHandle(), name);
		GLErrors.checkForError(TAG, "glGetUniformLocation: " + ((name != null) ? name : "null"));
		if (logAnError && uniformLocation < 0) {
			Log.error(TAG, "GetUniform failed: " + name);
		}
		return uniformLocation;
	}

	//<editor-fold desc="UNIFORM-SETTERS">
	/**
	 * Sets the value of the given uniform to the given integer array
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the integer array
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final int[] value, boolean onlyTry) {
		ensureCompiled();
		final int uniformLocation = getUniform(name, !onlyTry);
		if (uniformLocation < 0 && onlyTry) {
			return -1;
		}
		glUniform1iv(uniformLocation, value);
		return GLErrors.checkForError(TAG, "setUniformIntArray") ? -1 : uniformLocation;
	}


	public int setUniform(final String name, final int value) {
		return setUniform(name, value, false);
	}

	/**
	 * Sets the value of the given uniform to the given integer
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the integer
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final int value, boolean onlyTry) {
		return setUniform(name, new int[] {value}, onlyTry);
	}

	/**
	 * Sets the value of the given uniform to the given float array
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the float array
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final float[] value) {
		ensureCompiled();
		final int uniformLocation = getUniform(name);
		glUniform1fv(uniformLocation, value);
		return GLErrors.checkForError(TAG, "setUniformFloatArray") ? -1 : uniformLocation;
	}

	/**
	 * Sets the value of the given uniform to the given float
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the float
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final float value) {
		return setUniform(name, new float[] {value});
	}

	/**
	 * Sets the value of the given uniform to the given vec2
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the vec2
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Vector2f value) {
		ensureCompiled();
		final int uniformLocation = getUniform(name);
		FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(Constants.VEC2_COMPONENTS);
		value.get(vectorBuffer);
		glUniform2fv(uniformLocation, vectorBuffer);
		return GLErrors.checkForError(TAG, "setUniformVector2f") ? -1 : uniformLocation;
	}

	/**
	 * Sets the value of the given uniform to the given vec3
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the vec3
	 * @param onlyTry
	 *            whether the shader should only attempt to set the uniform
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Vector3f value, final boolean onlyTry) {
		ensureCompiled();
		final int uniformLocation = getUniform(name, !onlyTry);
		if (!onlyTry || uniformLocation >= 0) {
			FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(Constants.VEC3_COMPONENTS);
			value.get(vectorBuffer);
			glUniform3fv(uniformLocation, vectorBuffer);
			return GLErrors.checkForError(TAG, "setUniformVector3f") ? -1 : uniformLocation;
		} else {
			return uniformLocation;
		}
	}

	/**
	 * Attempts to set the given uniform to the given vec3
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the vec3
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Vector3f value) {
		return setUniform(name, value, false);
	}

	/**
	 * Sets the value of the given uniform to the given vec4
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the vec4
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Vector4f value) {
		ensureCompiled();
		final int uniformLocation = getUniform(name);
		FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(Constants.VEC4_COMPONENTS);
		value.get(vectorBuffer);
		glUniform4fv(uniformLocation, vectorBuffer);
		return GLErrors.checkForError(TAG, "setUniformVector4f") ? -1 : uniformLocation;
	}

	/**
	 * Sets the value of the given uniform the given array of 3x3 matrices and transposes them if transpose is true.
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the array of matrices
	 * @param transpose
	 *            whether to transpose the matrices
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Matrix3f[] value, final boolean transpose) {
		ensureCompiled();
		final int uniformLocation = getUniform(name);
		FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(value.length * Constants.MAT3_COMPONENTS);
		for (int i = 0; i < value.length; i++) {
			Matrix3f matrix = value[i];
			matrix.get(i * Constants.MAT3_COMPONENTS, matrixBuffer);
		}
		glUniformMatrix3fv(uniformLocation, transpose, matrixBuffer);
		return GLErrors.checkForError(TAG, "setUniformMatrix3fArray") ? -1 : uniformLocation;
	}

	/**
	 * Sets the value of the given uniform the given array of 3x3 matrices.
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the array of matrices
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Matrix3f[] value) {
		return setUniform(name, value, false);
	}

	/**
	 * Sets the value of the given uniform the given 3x3 matrix.
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the matrix
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Matrix3f value) {
		return setUniform(name, new Matrix3f[] {value}, false);
	}

	/**
	 * Sets the value of the given uniform the given array of 4x4 matrices and transposes them if transpose is true.
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the array of matrices
	 * @param transpose
	 *            whether to transpose the matrices
	 * @param onlyTry
	 *            whether the shader shuld only attempt to set the uniform
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Matrix4f[] value, final boolean transpose, final boolean onlyTry) {
		ensureCompiled();
		final int uniformLocation = getUniform(name, !onlyTry);
		if (!onlyTry || uniformLocation >= 0) {
			FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(Constants.MAT4_COMPONENTS * value.length);
			for (int i = 0; i < value.length; i++) {
				Matrix4f matrix = value[i];
				matrix.get(i * Constants.MAT4_COMPONENTS, matrixBuffer);
			}
			glUniformMatrix4fv(uniformLocation, transpose, matrixBuffer);
			return GLErrors.checkForError(TAG, "setUniformMatrix4fArray") ? -1 : uniformLocation;
		} else {
			return uniformLocation;
		}
	}

	/**
	 * Attempts the value of the given uniform the given array of 4x4 matrices.
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the array of matrices
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Matrix4f[] value) {
		return setUniform(name, value, false, false);
	}

	/**
	 * Attempts the value of the given uniform the given 4x4 matrix.
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the matrix
	 * @param onlyTry
	 *            whether the shader shuld only attempt to set the uniform
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Matrix4f value, final boolean onlyTry) {
		return setUniform(name, new Matrix4f[] {value}, false, onlyTry);
	}

	/**
	 * Attempts the value of the given uniform the given 4x4 matrix.
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the matrix
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Matrix4f value) {
		return setUniform(name, value, false);
	}

	/**
	 * Attempts to set the value of the given uniform to the given texture.
	 *
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the texture
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Texture value) {
		return setUniform(name, value, true, false);
	}

	/**
	 * Attempts to set the value of the given uniform to the given texture.
	 * 
	 * @param name
	 *            the name of the uniform
	 * @param value
	 *            the texture
	 * @param forceBind
	 *            whether the texture will be bound if it is not already bound
	 * @return the location of the set uniform or -1 if an error occurred
	 */
	public int setUniform(final String name, final Texture value, final boolean forceBind, final boolean onlyTry) {
		if (!value.isBound()) {
			if (forceBind) {
				if (!value.bind()) {
					Log.error(TAG, "Cannot set unbound texture as uniform.");
					return -1;
				}
			} else {
				Log.error(TAG, "Cannot set unbound texture as uniform.");
				return -1;
			}
		}
		return setUniform(name, value.getTextureUnit(), onlyTry);
	}

	//</editor-fold>

	public String getParameter(final String name) {
		return preprocessor.getParameter(name);
	}

	/**
	 * updates a shader-parameter, which triggers re-compiling on next usage
	 *
	 * @param name
	 *            the parameter name, like used in the shader file
	 * @param value
	 *            the value of the parameter
	 */
	public void updateParameter(final String name, final Object value) {
		preprocessor.updateParameter(name, value.toString());
	}

	/**
	 * compiles the shaderProgram with the current values of the parameters-Map and links it
	 */
	public void compile() {
		//compiling of all the shaderObjects happens lazily

		this.ensureExists();
		shaderObjects.values().forEach(shaderObject -> {
			if (shaderObject.wasCompiled()) {
				shaderObject.detachShader(this);
			}
			shaderObject.attachShader(this);
		});

		this.compiled = true;

		glLinkProgram(this.getHandle());
		final IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL20.glGetProgramiv(this.getHandle(), GL_LINK_STATUS, buffer);
		if (buffer.get(0) != 1) {
			Log.error(TAG, "Error linking shader program: " + buffer.get(0));
			Log.error(TAG, "Linking log:\n" + glGetProgramInfoLog(this.getHandle()));
		}

		for (final Map.Entry<String, Integer> entry : uniformBlocks.entrySet()) {
			this.setUniformBlockIndex(entry.getKey(), entry.getValue());
		}

		shaderObjects.values().forEach(ShaderObject::delete);
	}

	/**
	 * Setter for the drawMode
	 *
	 * @param drawMode
	 *            new drawMode
	 */
	public void setDrawMode(final DrawMode drawMode) {
		this.drawMode = drawMode;
	}

	/**
	 * Getter for the drawMode
	 *
	 * @return current drawmode. Most likely GL_TRIANGLES
	 */
	public DrawMode getDrawMode() {
		return drawMode;
	}

	/**
	 * @return whether this program is already compiled
	 */
	public boolean isCompiled() {
		return compiled;
	}

	/**
	 * ensure that this shaderProgram is in compiled state
	 */
	private void ensureCompiled() {
		if (!isCompiled() || preprocessor.areParametersDirty()) {
			compile();
		}
	}

	/**
	 * ensure that this shader program is up-to-date with the
	 * parameter-dictionary
	 */
	private void ensureUpToDate() {
		ensureCompiled();
		if (preprocessor.areParametersDirty()) {
			compile();
		}
	}

	/**
	 * Returns the name of the vertex shader. Might be useful for debugging
	 * 
	 * @return the name of the vertex shader. e.g: gui.vert -> gui
	 */
	public String getVertexName() {
		return shaderObjects.get(ShaderObjectType.VERTEX).getSourcePath().split("[.]")[0];
	}
}
