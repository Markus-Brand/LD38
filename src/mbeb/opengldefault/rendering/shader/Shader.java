package mbeb.opengldefault.rendering.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;

import java.net.*;
import java.nio.*;
import java.util.*;

import mbeb.opengldefault.logging.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

/**
 * Shader Object used for rendering a
 * {@link mbeb.opengldefault.rendering.renderable.IRenderable}
 *
 * @author Markus
 */
public class Shader {

	private static final String TAG = "Shader";

	/** OpenGL shader program */
	private int shaderProgram;
	/** Vertex Shaders source code */
	private final String vertexSource;
	/** Fragment Shaders source code */
	private final String fragmentSource;
	/** Geometry Shaders source code */
	private String geometrySource;
	/** Tessellation Control Shaders source code */
	private String tesControlSource;
	/** Tessellation Evaluation Shaders source code */
	private String tesEvalSource;
	/** Drawmode for the Renderables that get rendered by this shader */
	private int drawMode;

	/**
	 * Static parameters that can be changed by recompiling the shaders. They
	 * will be written into the shader via preprocessors #define
	 */
	private final Map<String, Object> parameters;
	/**
	 * dirty flag for parameters
	 */
	private boolean parametersChanged;
	/**
	 * Uniform Blocks used in the shader. Will hold data like projection and
	 * view matrices that are available to multiple shaders
	 */
	private final Map<Integer, String> uniformBlocks;

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath path of a vertex Shader
	 * @param fragmentPath path of a fragment Shader
	 */
	public Shader(final String vertexPath, final String fragmentPath) {
		this(vertexPath, fragmentPath, null, null, null, new HashMap<>());
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath path of a vertex Shader
	 * @param fragmentPath path of a fragment Shader
	 * @param parameters a map containing initial values for shader parameters
	 */
	public Shader(final String vertexPath, final String fragmentPath, final Map<String, Object> parameters) {
		this(vertexPath, fragmentPath, null, null, null, parameters);
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath path of a vertex Shader
	 * @param fragmentPath path of a fragment Shader
	 * @param geometryPath path of a geometry Shader
	 */
	public Shader(final String vertexPath, final String fragmentPath, final String geometryPath) {
		this(vertexPath, fragmentPath, geometryPath, null, null, new HashMap<>());
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath path of a vertex Shader
	 * @param fragmentPath path of a fragment Shader
	 * @param geometryPath path of a geometry Shader
	 * @param parameters a map containing initial values for shader parameters
	 */
	public Shader(final String vertexPath, final String fragmentPath, final String geometryPath, final Map<String, Object> parameters) {
		this(vertexPath, fragmentPath, geometryPath, null, null, parameters);
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath path of a vertex Shader
	 * @param fragmentPath path of a fragment Shader
	 * @param geometryPath path of a geometry Shader
	 * @param tesControlPath path of a tessellation control Shader
	 * @param tesEvalPath path of a tessellation evaluation Shader
	 */
	public Shader(final String vertexPath, final String fragmentPath, final String geometryPath, final String tesControlPath, final String tesEvalPath) {
		this(vertexPath, fragmentPath, geometryPath, tesControlPath, tesEvalPath, new HashMap<>());
	}

	/**
	 * main constructor of a shader object.
	 *
	 * @param vertexPath path of a vertex Shader
	 * @param fragmentPath path of a fragment Shader
	 * @param geometryPath path of a geometry Shader
	 * @param tesControlPath path of a tessellation control Shader
	 * @param tesEvalPath path of a tessellation evaluation Shader
	 * @param parameters a map containing initial values for shader parameters
	 */
	public Shader(final String vertexPath, final String fragmentPath, final String geometryPath, final String tesControlPath, final String tesEvalPath, final Map<String, Object> parameters) {
		this.parameters = parameters;
		this.vertexSource = getSource(vertexPath);
		this.fragmentSource = getSource(fragmentPath);
		if (geometryPath != null) {
			this.geometrySource = getSource(geometryPath);
		}
		if (tesControlPath != null) {
			this.tesControlSource = getSource(tesControlPath);
		}
		if (tesEvalPath != null) {
			this.tesEvalSource = getSource(tesEvalPath);
		}
		uniformBlocks = new HashMap<>();

		shaderProgram = -1;
		parametersChanged = true;
	}

	/**
	 * returns the Shader Source of a given path
	 *
	 * @param path
	 * @return
	 */
	public static String getSource(final String path) {
		try {
			final URL shaderURL = ClassLoader.getSystemResource("mbeb/opengldefault/shader/" + path).toURI().toURL();
			final Scanner sc = new Scanner(shaderURL.openStream(), "UTF-8");
			final String val = sc.useDelimiter("\\A").next();
			sc.close();
			return val;
		} catch (final Exception ex) {
			Log.error(TAG, "Loading shader source failed:" + path + "\n", ex);
			return "";
		}
	}

	/**
	 * Adds a Uniform Buffer Block to the Shader
	 *
	 * @param index
	 * @param name
	 */
	public void addUniformBlockIndex(final int index, final String name) {
		uniformBlocks.put(index, name);
		setUniformBlockIndex(index, name);
	}

	/**
	 * binds the uniform Block to the shader program
	 *
	 * @param index
	 * @param name
	 */
	private void setUniformBlockIndex(final int index, final String name) {
		ensureCompiled();
		final int uniformBlockIndex = glGetUniformBlockIndex(shaderProgram, name);
		glUniformBlockBinding(shaderProgram, uniformBlockIndex, index);
		GLErrors.checkForError(TAG, "glUniformBlockBinding");
	}

	/**
	 * use Shader
	 */
	public void use() {
		ensureUpToDate();
		glUseProgram(shaderProgram);
		GLErrors.checkForError(TAG, "glUseProgram");
	}

	/**
	 * get the location of an Uniform with given name
	 *
	 * @param name name of the uniform
	 * @return the uniforms location
	 */
	public int getUniform(final String name) {
		return getUniform(name, true);
	}

	/**
	 * get the location of a Uniform with given name
	 *
	 * @param name name of the uniform
	 * @param logAnError log an error if not found
	 * @return the uniforms location
	 */
	public int getUniform(final String name, final boolean logAnError) {
		ensureCompiled();
		final int loc = glGetUniformLocation(shaderProgram, name);
		GLErrors.checkForError(TAG, "glGetUniformLocation: " + ((name != null) ? name : "null"));
		if (logAnError && loc < 0) {
			Log.error(TAG, "GetUniform failed: " + name);
		}
		return loc;
	}

	public String getParameter(final String name) {
		final Object obj = parameters.get(name);
		return obj == null ? "" : obj.toString();
	}

	/**
	 * update a shader-parameter, which triggers re-compiling on next usage
	 *
	 * @param name the parameter name, like used in the shader file
	 * @param value the value of the parameter
	 */
	public void updateParameter(final String name, final Object value) {
		parameters.put(name, value);
		parametersChanged = true;
	}

	/**
	 * compiles the shader with the current values of the parameters-Map
	 */
	public void compile() {
		// generating parameters precompiler actions
		parametersChanged = false;
		StringBuilder headerBuilder = new StringBuilder("#version 330 core ").append(System.getProperty("line.separator"));
		for (final String key : parameters.keySet()) {
			final String value = getParameter(key);
			headerBuilder.append("#define ").append(key).append(" ").append(value).append(System.getProperty("line.separator"));
		}
		String paramString = headerBuilder.toString();

		final int vertexShader = compileVertexShader(paramString);
		final int fragmentShader = compileFragmentShader(paramString);
		final int geomShader = compileGeometryShader(paramString);
		final int tesControlShader = compileTesControlShader(paramString);
		final int tesEvalShader = compileTesEvalShader(paramString);

		linkShader(vertexShader, fragmentShader, geomShader, tesControlShader, tesEvalShader);

		for (Map.Entry<Integer, String> uniformBlockBinding : uniformBlocks.entrySet()) {
			setUniformBlockIndex(uniformBlockBinding.getKey(), uniformBlockBinding.getValue());
		}
	}

	/**
	 * Compiles Vertex Shader
	 *
	 * @param paramString Shader Header
	 * @return vertex shader object
	 */
	private int compileVertexShader(final String paramString) {
		final int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, paramString + vertexSource);
		glCompileShader(vertexShader);
		final int compileSuccess = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling vertex shader: " + compileSuccess);
			Log.error(TAG, "Vertex log:\n" + glGetShaderInfoLog(vertexShader, 512));
		}
		return vertexShader;
	}

	/**
	 * Compiles Fragment Shader
	 *
	 * @param paramString Shader Header
	 * @return fragment shader object
	 */
	private int compileFragmentShader(final String paramString) {
		final int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, paramString + fragmentSource);
		glCompileShader(fragmentShader);
		final int compileSuccess = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling fragment shader: " + compileSuccess);
			Log.error(TAG, "Fragment log:\n" + glGetShaderInfoLog(fragmentShader, 512));
		}
		return fragmentShader;
	}

	/**
	 * Compiles Geometry Shader
	 *
	 * @param paramString Shader Header
	 * @return geometry shader object
	 */
	private int compileGeometryShader(final String paramString) {
		if (geometrySource == null) {
			return -1;
		}
		final int geometryShader = glCreateShader(GL_GEOMETRY_SHADER);
		glShaderSource(geometryShader, paramString + geometrySource);
		glCompileShader(geometryShader);
		final int compileSuccess = glGetShaderi(geometryShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling geometry shader: " + compileSuccess);
			Log.error(TAG, "Geometry log:\n" + glGetShaderInfoLog(geometryShader, 512));
		}
		return geometryShader;
	}

	/**
	 * Compiles Tessellation Control Shader
	 *
	 * @param paramString Shader Header
	 * @return tessellation control shader object
	 */
	private int compileTesControlShader(final String paramString) {
		if (tesControlSource == null) {
			return -1;
		}
		final int tesControlShader = glCreateShader(GL_TESS_CONTROL_SHADER);
		glShaderSource(tesControlShader, paramString + tesControlSource);
		glCompileShader(tesControlShader);
		final int compileSuccess = glGetShaderi(tesControlShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling tessellation control shader: " + compileSuccess);
			Log.error(TAG, "Tessellation Control log:\n" + glGetShaderInfoLog(tesControlShader, 512));
		}
		return tesControlShader;
	}

	/**
	 * Compiles Tessellation Evaluation Shader
	 *
	 * @param paramString Shader Header
	 * @return tessellation evaluation shader object
	 */
	private int compileTesEvalShader(final String paramString) {
		if (tesEvalSource == null) {
			return -1;
		}
		final int tesEvalShader = glCreateShader(GL_TESS_EVALUATION_SHADER);
		glShaderSource(tesEvalShader, paramString + tesEvalSource);
		glCompileShader(tesEvalShader);
		final int compileSuccess = glGetShaderi(tesEvalShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling tessellation evaluation shader: " + compileSuccess);
			Log.error(TAG, "Tessellation Evaluation log:\n" + glGetShaderInfoLog(tesEvalShader, 512));
		}
		return tesEvalShader;
	}

	/**
	 * links the shader program
	 *
	 * @param vertexShader vertex shader object
	 * @param fragmentShader fragment shader object
	 * @param tesControlShader
	 * @param tesEvalShader
	 * @param geomShader
	 */
	private void linkShader(final int vertexShader, final int fragmentShader, final int geomShader, final int tesControlShader, final int tesEvalShader) {
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexShader);
		glAttachShader(shaderProgram, fragmentShader);
		if (geomShader != -1) {
			glAttachShader(shaderProgram, geomShader);
		}
		if (tesControlShader != -1) {
			glAttachShader(shaderProgram, tesControlShader);
		}
		if (tesEvalShader != -1) {
			glAttachShader(shaderProgram, tesEvalShader);
		}
		glLinkProgram(shaderProgram);
		final IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL20.glGetProgramiv(shaderProgram, GL_LINK_STATUS, buffer);
		if (buffer.get(0) != 1) {
			Log.error(TAG, "Error linking shader program: " + buffer.get(0));
			Log.error(TAG, "Linking log:\n" + glGetProgramInfoLog(shaderProgram, 512));
		}

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	/**
	 * Setter for the drawMode
	 *
	 * @param drawMode new drawMode
	 */
	public void setDrawMode(int drawMode) {
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
		return shaderProgram >= 0;
	}

	/**
	 * ensure that this shader is in compiled state
	 */
	private void ensureCompiled() {
		if (!isCompiled() || parametersChanged) {
			compile();
		}
	}

	/**
	 * ensure that this shader program is up-to-date with the
	 * parameter-dictionary
	 */
	private void ensureUpToDate() {
		ensureCompiled();
		if (parametersChanged) {
			compile();
		}
	}
}
