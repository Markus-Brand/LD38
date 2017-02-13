package mbeb.opengldefault.rendering.shader;

import java.net.URL;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Shader Object used for rendering a {@link mbeb.opengldefault.rendering.renderable.IRenderable}
 * 
 * @author Markus
 *
 */
public class Shader {

	/** Class Name Tag */
	private static final String TAG = "Shader";

	/** OpenGL shader program */
	private int shaderProgram;
	/** Vertex Shaders source code */
	private String vertexSource;
	/** Fragment Shaders source code */
	private String fragmentSource;
	/** Geometry Shaders source code */
	private String geometrySource;
	/** Tesselation Control Shaders source code */
	private String tesControlSource;
	/** Tesselation Evaluation Shaders source code */
	private String tesEvalSource;

	/** Static parameters that can be changed by recompiling the shaders. They will be written into the shader via preprocessors #define */
	private Map<String, Object> parameters;
	/** Uniform Blocks used in the shader. Will hold data like projection and view matrices that are available to multiple shaders */
	private Map<Integer, String> uniformBlocks;

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath
	 *            path of a vertex Shader
	 * @param fragmentPath
	 *            path of a fragment Shader
	 */
	public Shader(String vertexPath, String fragmentPath) {
		this(vertexPath, fragmentPath, null, null, null, new HashMap<>());
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath
	 *            path of a vertex Shader
	 * @param fragmentPath
	 *            path of a fragment Shader
	 * @param parameters
	 *            a map containing initial values for shader parameters
	 */
	public Shader(String vertexPath, String fragmentPath, Map<String, Object> parameters) {
		this(vertexPath, fragmentPath, null, null, null, parameters);
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath
	 *            path of a vertex Shader
	 * @param fragmentPath
	 *            path of a fragment Shader
	 * @param geometryPath
	 *            path of a geometry Shader
	 */

	public Shader(String vertexPath, String fragmentPath, String geometryPath) {
		this(vertexPath, fragmentPath, geometryPath, null, null, new HashMap<>());
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath
	 *            path of a vertex Shader
	 * @param fragmentPath
	 *            path of a fragment Shader
	 * @param geometryPath
	 *            path of a geometry Shader
	 * @param parameters
	 *            a map containing initial values for shader parameters
	 */
	public Shader(String vertexPath, String fragmentPath, String geometryPath, Map<String, Object> parameters) {
		this(vertexPath, fragmentPath, geometryPath, null, null, parameters);
	}

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath
	 *            path of a vertex Shader
	 * @param fragmentPath
	 *            path of a fragment Shader
	 * @param geometryPath
	 *            path of a geometry Shader
	 * @param tesControlPath
	 *            path of a tesselation control Shader
	 * @param tesEvalPath
	 *            path of a tesselation evaluation Shader
	 */
	public Shader(String vertexPath, String fragmentPath, String geometryPath, String tesControlPath, String tesEvalPath) {
		this(vertexPath, fragmentPath, geometryPath, tesControlPath, tesEvalPath, new HashMap<>());
	}

	/**
	 * main constructor of a shader object.
	 *
	 * @param vertexPath
	 *            path of a vertex Shader
	 * @param fragmentPath
	 *            path of a fragment Shader
	 * @param geometryPath
	 *            path of a geometry Shader
	 * @param tesControlPath
	 *            path of a tesselation control Shader
	 * @param tesEvalPath
	 *            path of a tesselation evaluation Shader
	 * @param parameters
	 *            a map containing initial values for shader parameters
	 */
	public Shader(String vertexPath, String fragmentPath, String geometryPath, String tesControlPath, String tesEvalPath, Map<String, Object> parameters) {
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
		uniformBlocks = new HashMap<Integer, String>();

		compile();
	}

	/**
	 * returns the Shader Source of a given path
	 *
	 * @param path
	 * @return
	 */
	public static String getSource(String path) {
		try {
			URL shaderURL = ClassLoader.getSystemResource("mbeb/opengldefault/shader/" + path).toURI().toURL();
			Scanner sc = new Scanner(shaderURL.openStream(), "UTF-8");
			String val = sc.useDelimiter("\\A").next();
			sc.close();
			return val;
		} catch (Exception ex) {
			Log.error(TAG, "Loading shader source failed:" + path + "\n" + ex.getMessage());
			return "";
		}
	}

	/**
	 * Adds a Uniform Buffer Block to the Shader
	 *
	 * @param index
	 * @param name
	 */
	public void addUniformBlockIndex(int index, String name) {
		uniformBlocks.put(index, name);
		setUniformBlockIndex(index, name);
	}

	/**
	 * binds the uniform Block to the shader program
	 *
	 * @param index
	 * @param name
	 */
	private void setUniformBlockIndex(int index, String name) {
		int uniformBlockIndex = glGetUniformBlockIndex(shaderProgram, name);
		glUniformBlockBinding(shaderProgram, uniformBlockIndex, index);
		GLErrors.checkForError(TAG, "glUniformBlockBinding");
	}

	/**
	 * use Shader
	 */
	public void use() {
		glUseProgram(shaderProgram);
		GLErrors.checkForError(TAG, "glUseProgram");
	}

	/**
	 * get the location of a Uniform with given name
	 *
	 * @param name
	 *            name of the uniform
	 * @return the uniforms location
	 */
	public int getUniform(String name) {
		return getUniform(name, true);
	}
	
	/**
	 * get the location of a Uniform with given name
	 * @param name name of the uniform
	 * @param logAnError log an error if not found
	 * @return the uniforms location
	 */
	public int getUniform(String name, boolean logAnError) {
		int loc = glGetUniformLocation(shaderProgram, name);
		GLErrors.checkForError(TAG, "glGetUniformLocation");
		if (logAnError && loc < 0) {
			Log.error(TAG, "GetUniform failed: " + name);
		}
		return loc;
	}

	public String getParameter(String name) {
		Object obj = parameters.get(name);
		return obj == null ? "" : obj.toString();
	}

	/**
	 * update a shader-parameter
	 *
	 * @param name
	 *            the parameter name, like used in the shader file
	 * @param value
	 *            the value of the parameter
	 * @param update
	 *            wether to directly re-compile the shader after (when false is
	 *            passed, call shader.compile() to view the results)
	 */
	public void updateParameter(String name, Object value, boolean update) {
		parameters.put(name, value);
		if (update) {
			compile();
		}
	}

	/**
	 * updates a parameter and instantly recompiles the Shader
	 *
	 * @param name
	 * @param value
	 */
	public void updateParameter(String name, Object value) {
		updateParameter(name, value, true);
	}

	/**
	 * compiles the shader with the current values of the parameters-Map
	 */
	public void compile() {
		// generating parameters precompiler actions
		String paramString = "#version 330 core " + System.getProperty("line.separator");
		for (String key : parameters.keySet()) {
			String value = getParameter(key);
			paramString += "#define " + key + " " + value + System.getProperty("line.separator");
		}

		int vertexShader = compileVertexShader(paramString);
		int fragmentShader = compileFragmetShader(paramString);
		int geomShader = compileGeometryShader(paramString);
		int tesControlShader = compileTesControlShader(paramString);
		int tesEvalShader = compileTesEvalShader(paramString);

		linkShader(vertexShader, fragmentShader, geomShader, tesControlShader, tesEvalShader);

		for (int key : uniformBlocks.keySet()) {
			setUniformBlockIndex(key, uniformBlocks.get(key));
			GLErrors.checkForError(TAG, "setUniformBlockIndex");
		}
	}

	/**
	 * Compiles Vertex Shader
	 *
	 * @param paramString
	 *            Shader Header
	 * @return vertex shader object
	 */
	private int compileVertexShader(String paramString) {
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, paramString + vertexSource);
		glCompileShader(vertexShader);
		int compileSuccess = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compilng vertex shader: " + compileSuccess);
			Log.error(TAG, "Vertex log:\n" + glGetShaderInfoLog(vertexShader, 512));
		}
		return vertexShader;
	}

	/**
	 * Compiles Fragment Shader
	 *
	 * @param paramString
	 *            Shader Header
	 * @return fragment shader object
	 */
	private int compileFragmetShader(String paramString) {
		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, paramString + fragmentSource);
		glCompileShader(fragmentShader);
		int compileSuccess = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling fragment shader: " + compileSuccess);
			Log.error(TAG, "Fragment log:\n" + glGetShaderInfoLog(fragmentShader, 512));
		}
		return fragmentShader;
	}

	/**
	 * Compiles Geometry Shader
	 *
	 * @param paramString
	 *            Shader Header
	 * @return geometry shader object
	 */
	private int compileGeometryShader(String paramString) {
		if (geometrySource == null) {
			return -1;
		}
		int geometryShader = glCreateShader(GL_GEOMETRY_SHADER);
		glShaderSource(geometryShader, paramString + geometrySource);
		glCompileShader(geometryShader);
		int compileSuccess = glGetShaderi(geometryShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling geometry shader: " + compileSuccess);
			Log.error(TAG, "Geometry log:\n" + glGetShaderInfoLog(geometryShader, 512));
		}
		return geometryShader;
	}

	/**
	 * Compiles Tesselation Control Shader
	 *
	 * @param paramString
	 *            Shader Header
	 * @return tesselation control shader object
	 */
	private int compileTesControlShader(String paramString) {
		if (tesControlSource == null) {
			return -1;
		}
		int tesControlShader = glCreateShader(GL_TESS_CONTROL_SHADER);
		glShaderSource(tesControlShader, paramString + tesControlSource);
		glCompileShader(tesControlShader);
		int compileSuccess = glGetShaderi(tesControlShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling tesselation control shader: " + compileSuccess);
			Log.error(TAG, "Tesselation Control log:\n" + glGetShaderInfoLog(tesControlShader, 512));
		}
		return tesControlShader;
	}

	/**
	 * Compiles Tesselation Evaluation Shader
	 *
	 * @param paramString
	 *            Shader Header
	 * @return tesselation evaluation shader object
	 */
	private int compileTesEvalShader(String paramString) {
		if (tesEvalSource == null) {
			return -1;
		}
		int tesEvalShader = glCreateShader(GL_TESS_EVALUATION_SHADER);
		glShaderSource(tesEvalShader, paramString + tesEvalSource);
		glCompileShader(tesEvalShader);
		int compileSuccess = glGetShaderi(tesEvalShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling tesselation evaluation shader: " + compileSuccess);
			Log.error(TAG, "Tesselation Evaluation log:\n" + glGetShaderInfoLog(tesEvalShader, 512));
		}
		return tesEvalShader;
	}

	/**
	 * links the shader program
	 *
	 * @param vertexShader
	 *            vertex shader object
	 * @param fragmentShader
	 *            fragment shader object
	 * @param tesControlShader
	 * @param tesEvalShader
	 * @param geomShader
	 */
	private void linkShader(int vertexShader, int fragmentShader, int geomShader, int tesControlShader, int tesEvalShader) {
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
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL20.glGetProgramiv(shaderProgram, GL_LINK_STATUS, buffer);
		if (buffer.get(0) != 1) {
			Log.error(TAG, "Error linking shader program: " + buffer.get(0));
			Log.error(TAG, "Linking log:\n" + glGetProgramInfoLog(shaderProgram, 512));
		}

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}
}
