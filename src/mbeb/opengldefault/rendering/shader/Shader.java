package mbeb.opengldefault.rendering.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;

import java.nio.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mbeb.opengldefault.logging.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

/**
 * Shader Object used for rendering a {@link mbeb.opengldefault.rendering.renderable.IRenderable}
 *
 * @author Markus
 */
public class Shader {

	private static final String TAG = "Shader";

	/**
	 * OpenGL shader program
	 */
	private int shaderProgram;
	/**
	 * Vertex Shaders source code
	 */
	private final String vertexPath;
	/**
	 * Fragment Shaders source code
	 */
	private final String fragmentPath;
	/**
	 * Geometry Shaders source code
	 */
	private String geometryPath;
	/**
	 * Tessellation Control Shaders source code
	 */
	private String tesControlPath;
	/**
	 * Tessellation Evaluation Shaders source code
	 */
	private String tesEvalPath;
	/**
	 * Drawmode for the Renderables that get rendered by this shader
	 */
	private int drawMode;

	/**
	 * handles current parameter set and offers precompilation of Shader-Files
	 */
	private ShaderPreprocessor preprocessor;

	/**
	 * Uniform Blocks used in the shader. Will hold data like projection and
	 * view matrices that are available to multiple shaders
	 */
	private final Map<Integer, String> uniformBlocks;

	/**
	 * constructor of a shader object.
	 *
	 * @param vertexPath
	 *            path of a vertex Shader
	 * @param fragmentPath
	 *            path of a fragment Shader
	 */
	public Shader(final String vertexPath, final String fragmentPath) {
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
	public Shader(final String vertexPath, final String fragmentPath, final Map<String, Object> parameters) {
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
	public Shader(final String vertexPath, final String fragmentPath, final String geometryPath) {
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
	public Shader(final String vertexPath, final String fragmentPath, final String geometryPath,
			final Map<String, Object> parameters) {
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
	 *            path of a tessellation control Shader
	 * @param tesEvalPath
	 *            path of a tessellation evaluation Shader
	 */
	public Shader(final String vertexPath, final String fragmentPath, final String geometryPath,
			final String tesControlPath, final String tesEvalPath) {
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
	 *            path of a tessellation control Shader
	 * @param tesEvalPath
	 *            path of a tessellation evaluation Shader
	 * @param parameters
	 *            a map containing initial values for shader parameters
	 */
	public Shader(final String vertexPath, final String fragmentPath, final String geometryPath,
			final String tesControlPath, final String tesEvalPath, final Map<String, Object> parameters) {
		this.vertexPath = vertexPath;
		this.fragmentPath = fragmentPath;
		this.geometryPath = geometryPath;
		this.tesControlPath = tesControlPath;
		this.tesEvalPath = tesEvalPath;

		uniformBlocks = new HashMap<>();
		shaderProgram = -1;
		this.preprocessor = new ShaderPreprocessor(parameters);
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
		final int loc = glGetUniformLocation(shaderProgram, name);
		GLErrors.checkForError(TAG, "glGetUniformLocation: " + (name != null ? name : "null"));
		if (logAnError && loc < 0) {
			Log.error(TAG, vertexPath + " GetUniform failed: " + name);
		}
		return loc;
	}

	public String getParameter(final String name) {
		return preprocessor.getParameter(name);
	}

	/**
	 * update a shader-parameter, which triggers re-compiling on next usage
	 *
	 * @param name
	 *            the parameter name, like used in the shader file
	 * @param value
	 *            the value of the parameter
	 */
	public void updateParameter(final String name, final Object value) {
		preprocessor.updateParameter(name, value);
	}

	/**
	 * compiles the shader with the current values of the parameters-Map
	 */
	public void compile() {
		final int vertexShader = compileVertexShader();
		final int fragmentShader = compileFragmentShader();
		final int geomShader = compileGeometryShader();
		final int tesControlShader = compileTesControlShader();
		final int tesEvalShader = compileTesEvalShader();

		linkShader(vertexShader, fragmentShader, geomShader, tesControlShader, tesEvalShader);

		for (Map.Entry<Integer, String> uniformBlockBinding : uniformBlocks.entrySet()) {
			setUniformBlockIndex(uniformBlockBinding.getKey(), uniformBlockBinding.getValue());
		}
	}

	/**
	 * Compiles Vertex Shader
	 *
	 * @param paramString
	 *            Shader Header
	 * @return vertex shader object
	 */
	private int compileVertexShader() {
		String sourceString = preprocessor.getProcessedShaderFile(vertexPath);
		final int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, sourceString);
		glCompileShader(vertexShader);
		final int compileSuccess = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling vertex shader: " + compileSuccess);
			String log = glGetShaderInfoLog(vertexShader);
			printDebug(log, sourceString, vertexPath);
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
	private int compileFragmentShader() {
		String sourceString = preprocessor.getProcessedShaderFile(fragmentPath);
		final int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, sourceString);
		glCompileShader(fragmentShader);
		final int compileSuccess = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling fragment shader: " + compileSuccess);
			String log = glGetShaderInfoLog(fragmentShader);
			printDebug(log, sourceString, fragmentPath);
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
	private int compileGeometryShader() {
		if (geometryPath == null) {
			return -1;
		}
		String sourceString = preprocessor.getProcessedShaderFile(geometryPath);
		final int geometryShader = glCreateShader(GL_GEOMETRY_SHADER);
		glShaderSource(geometryShader, sourceString);
		glCompileShader(geometryShader);
		final int compileSuccess = glGetShaderi(geometryShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling geometry shader: " + compileSuccess);
			String log = glGetShaderInfoLog(geometryShader);
			printDebug(log, sourceString, geometryPath);
		}
		return geometryShader;
	}

	/**
	 * Compiles Tessellation Control Shader
	 *
	 * @param paramString
	 *            Shader Header
	 * @return tessellation control shader object
	 */
	private int compileTesControlShader() {
		if (tesControlPath == null) {
			return -1;
		}
		String sourceString = preprocessor.getProcessedShaderFile(tesControlPath);
		final int tesControlShader = glCreateShader(GL_TESS_CONTROL_SHADER);
		glShaderSource(tesControlShader, sourceString);
		glCompileShader(tesControlShader);
		final int compileSuccess = glGetShaderi(tesControlShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling tessellation control shader: " + compileSuccess);
			String log = glGetShaderInfoLog(tesControlShader);
			printDebug(log, sourceString, tesControlPath);
		}
		return tesControlShader;
	}

	/**
	 * Compiles Tessellation Evaluation Shader
	 *
	 * @param paramString
	 *            Shader Header
	 * @return tessellation evaluation shader object
	 */
	private int compileTesEvalShader() {
		if (tesEvalPath == null) {
			return -1;
		}
		String sourceString = preprocessor.getProcessedShaderFile(tesEvalPath);
		final int tesEvalShader = glCreateShader(GL_TESS_EVALUATION_SHADER);
		glShaderSource(tesEvalShader, sourceString);
		glCompileShader(tesEvalShader);
		final int compileSuccess = glGetShaderi(tesEvalShader, GL_COMPILE_STATUS);
		if (compileSuccess != 1) {
			Log.error(TAG, "Error compiling tessellation evaluation shader: " + compileSuccess);
			String log = glGetShaderInfoLog(tesEvalShader);
			printDebug(log, sourceString, tesEvalPath);
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
	private void linkShader(final int vertexShader, final int fragmentShader, final int geomShader,
			final int tesControlShader, final int tesEvalShader) {
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
			Log.error(TAG, "Linking log:\n" + glGetProgramInfoLog(shaderProgram));
		}

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	/**
	 * Print source code of shader object line by line with leading line
	 * numbers and in-place error messages
	 *
	 * @param log
	 *            the shader log info.
	 */
	private void printDebug(final String log, final String source, final String sourceName) {
		System.out.println(log);
		Log.error(TAG, "Errors in \"" + sourceName + "\":");

		// stores line number and error message
		final LinkedHashMap<Integer, ArrayList<String>> errorList = new LinkedHashMap<>();

		// regular expression for extracting error line and message for nearly all devices
		final String generalExp = "(?:ERROR: )?\\d+:\\(?(\\d+)\\)?: (.+)";
		final Pattern regExPattern = Pattern.compile(generalExp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		try(Scanner scannerShaderLog = new Scanner(log)) {
			while(scannerShaderLog.hasNextLine()) {
				final String logLine = scannerShaderLog.nextLine();
				final Matcher regExMatcher = regExPattern.matcher(logLine);
				if (regExMatcher.find()) {

					final String lineNumberString = regExMatcher.group(1);
					final String errorString = regExMatcher.group(2);
					final int lineNumber = Integer.parseInt(lineNumberString);

					if (errorList.containsKey(lineNumber)) {
						errorList.get(lineNumber).add(errorString);

					} else {
						final ArrayList<String> stringList = new ArrayList<>();
						stringList.add(errorString);
						errorList.put(lineNumber, stringList);
					}
				}
			}
		}

		// format source code
		final String errorNo = "    ";
		final String errorYes = "\\->>>>>>";
		final DecimalFormat numberFormat = new DecimalFormat("0000");

		try(Scanner scannerSourceCode = new Scanner(source)) {
			int lineNumber = 1;

			while(scannerSourceCode.hasNextLine()) {
				final String codeLine = scannerSourceCode.nextLine();
				String formattedLineNumber = numberFormat.format(lineNumber);
				final String annotatedLine = formattedLineNumber + errorNo + ": " + codeLine;
				Log.log(null, annotatedLine);
				if (errorList.containsKey(lineNumber)) {
					for (final String string : errorList.get(lineNumber)) {
						Log.log(null, errorYes + ": " + string);
					}
				}

				lineNumber++;
			}
		}
		throw new RuntimeException("Non-compilable shader code!");
	}

	/**
	 * Setter for the drawMode
	 *
	 * @param drawMode
	 *            new drawMode
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

	public String getVertexName() {
		return vertexPath.split("[.]")[0];
	}
}
