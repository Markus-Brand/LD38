package mbeb.opengldefault.rendering.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.*;
import java.util.*;

import mbeb.opengldefault.constants.Constants;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;

import mbeb.opengldefault.logging.*;

/**
 * Shader Object used for rendering a {@link mbeb.opengldefault.rendering.renderable.IRenderable}
 *
 * @author Markus, Erik
 */
public class ShaderProgram {

	private static final String TAG = "ShaderProgram";

	/** OpenGL shader program */
	private int shaderProgramHandle;
	/** Drawmode for the Renderables that get rendered by this shader */
	private int drawMode;
	/** the shader components */
	private Map<ShaderObjectType, ShaderObject> shaderObjects;

	/**
	 * handles current parameter set and offers precompilation of Shader-Files
	 */
	private final ShaderPreprocessor preprocessor;

	/**
	 * Uniform Blocks used in the shader. Will hold data like projection and
	 * view matrices that are available to multiple shaders
	 */
	private final Map<Integer, String> uniformBlocks;

	/**
	 * constructor of a shader object.
	 *
	 * @param shaders the paths to all shader objects
	 */
	public ShaderProgram(final String... shaders) {
		this(new HashMap<>(), shaders);
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param parameters
	 *            a map containing initial values for shader parameters
	 * @param shaders the paths to all shader objects
	 */
	public ShaderProgram(final Map<String, String> parameters, final String... shaders) {
		this.preprocessor = new ShaderPreprocessor(parameters);
		uniformBlocks = new HashMap<>();
		shaderProgramHandle = -1;

		shaderObjects = new HashMap<>();
		for (String path: shaders) {
			ShaderObject obj = new ShaderObject(path, preprocessor);
			shaderObjects.put(obj.getType(), obj);
		}

		Log.assertTrue(TAG, shaderObjects.containsKey(ShaderObjectType.VERTEX), "Vertex shader required");
		Log.assertTrue(TAG, shaderObjects.containsKey(ShaderObjectType.FRAGMENT), "Fragment shader required");
		Log.assertEqual(TAG, shaderObjects.containsKey(ShaderObjectType.TCS), shaderObjects.containsKey(ShaderObjectType.TES), "TCS and TES are only allowed together.");
	}

	/**
	 * Adds a Uniform Buffer Block to the Shader
	 *
	 * @param UBOName
	 *            the name of the UBO to add
	 */
	public void addUniformBlockIndex(final String UBOName) {
		final int index = UBOManager.getUBOID(UBOName);
		uniformBlocks.put(index, UBOName);
		setUniformBlockIndex(index, UBOName);
	}

	/**
	 * binds the uniform Block to the shader program
	 *
	 * @param index
	 * @param name
	 */
	private void setUniformBlockIndex(final int index, final String name) {
		ensureCompiled();
		final int uniformBlockIndex = glGetUniformBlockIndex(shaderProgramHandle, name);
		GLErrors.checkForError(TAG, "glGetUniformBlockIndex");
		glUniformBlockBinding(shaderProgramHandle, uniformBlockIndex, index);
		GLErrors.checkForError(TAG, "glUniformBlockBinding");
	}

	/**
	 * use Shader
	 */
	public void use() {
		ensureUpToDate();
		glUseProgram(shaderProgramHandle);
		GLErrors.checkForError(TAG, "glUseProgram");
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
		final int uniformLocation = glGetUniformLocation(shaderProgramHandle, name);
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
	public int setUniform(final String name, final int[] value) {
		ensureCompiled();
		final int uniformLocation = getUniform(name);
		glUniform1iv(uniformLocation, value);
		return GLErrors.checkForError(TAG, "setUniformIntArray") ? -1 : uniformLocation;
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
	public int setUniform(final String name, final int value) {
		return setUniform(name, new int[] {value});
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
		shaderProgramHandle = glCreateProgram();
		shaderObjects.values().forEach(shaderObject -> shaderObject.attachShader(shaderProgramHandle));

		glLinkProgram(shaderProgramHandle);
		final IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL20.glGetProgramiv(shaderProgramHandle, GL_LINK_STATUS, buffer);
		if (buffer.get(0) != 1) {
			Log.error(TAG, "Error linking shader program: " + buffer.get(0));
			Log.error(TAG, "Linking log:\n" + glGetProgramInfoLog(shaderProgramHandle));
		}

		for (final Map.Entry<Integer, String> uniformBlockBinding : uniformBlocks.entrySet()) {
			setUniformBlockIndex(uniformBlockBinding.getKey(), uniformBlockBinding.getValue());
		}

		shaderObjects.values().forEach(ShaderObject::delete);
	}

	/**
	 * Setter for the drawMode
	 *
	 * @param drawMode
	 *            new drawMode
	 */
	public void setDrawMode(final int drawMode) {
		this.drawMode = drawMode;
	}

	/**
	 * Getter for the drawMode
	 *
	 * @return current drawmode. Most likely GL_TRIANGLES
	 */
	public int getDrawMode() {
		if (drawMode <= 0) {
			drawMode = GL_TRIANGLES;
		}
		return drawMode;
	}

	/**
	 * @return whether this program is already compiled
	 */
	public boolean isCompiled() {
		return shaderProgramHandle >= 0;
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
	 * @return the name of the vertex shader. e.g: gui.vert -> gui
	 */
	public String getVertexName() {
		return shaderObjects.get(ShaderObjectType.VERTEX).getSourcePath().split("[.]")[0];
	}
}
