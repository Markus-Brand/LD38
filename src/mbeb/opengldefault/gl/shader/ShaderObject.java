package mbeb.opengldefault.gl.shader;

import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.logging.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL20.*;

/**
 * a single shader. Has a Type, can be compiled etc...
 */
public class ShaderObject {

	private static final String TAG = "ShaderObject";

	/** the type of this shader */
	private final ShaderObjectType type;
	/** the source path of this shader */
	private final String sourcePath;
	/** the preprocessor instance to fetch the source from */
	private final ShaderPreprocessor preprocessor;

	/** the gl-handle to the shader object. Only valid if compiled==true */
	private Integer shaderID = null;
	/** only true if this shader is currently compiled */
	private boolean compiled;


	/**
	 * create a new shader object, but do not compile it yet
	 * @param sourcePath the path to the source file
	 * @param preprocessor the preprocessor to parse the source file with
	 */
	public ShaderObject(String sourcePath, ShaderPreprocessor preprocessor) {
		this.sourcePath = sourcePath;
		this.preprocessor = preprocessor;
		this.type = ShaderObjectType.byName(sourcePath);
		compiled = false;
	}

	/**
	 * @return the type of this ShaderObject, determined by file extension
	 */
	public ShaderObjectType getType() {
		return type;
	}

	/**
	 * ensures that this shader is compiled
	 * @return a gl-handle to a shader object
	 */
	public int getCompiledShaderID() {
		if (!compiled) {
			shaderID = glCreateShader(getType().getGlType());
			GLErrors.checkForError(TAG, "glCreateShader");

			final String sourceString = preprocessor.getProcessedShaderFile(sourcePath);
			glShaderSource(shaderID, sourceString);
			GLErrors.checkForError(TAG, "glShaderSource");

			glCompileShader(shaderID);
			GLErrors.checkForError(TAG, "glCompileShader");

			final int compileSuccess = glGetShaderi(shaderID, GL_COMPILE_STATUS);
			GLErrors.checkForError(TAG, "glGetShaderi");

			if (compileSuccess != 1) {
				Log.error(TAG, "Error compiling vertex shader: " + compileSuccess);
				final String log = glGetShaderInfoLog(shaderID);
				GLErrors.checkForError(TAG, "glGetShaderInfoLog");
				printDebug(log, sourceString, sourcePath);
			}
			compiled = true;
		}
		return shaderID;
	}

	/**
	 * @return whether this shaderObject was already compiled before
	 */
	public boolean wasCompiled() {
		return shaderID != null;
	}

	/**
	 * @return the gl-handle for this object, or null if it was not compiled before
	 */
	public Integer getPreviousShaderID() {
		return shaderID;
	}

	/**
	 * Remove this shader again from a ShaderProgram
	 * @param program the program to remove it from
	 */
	public void detachShader(ShaderProgram program) {
		glDetachShader(program.getHandle(), this.getPreviousShaderID());
		GLErrors.checkForError(TAG, "glDetachShader");
	}

	/**
	 * attaches this shader to an openGL shader program
	 * @param program the program to attach to
	 */
	public void attachShader(ShaderProgram program) {
		glAttachShader(program.getHandle(), getCompiledShaderID());
		GLErrors.checkForError(TAG, "glAttachShader - " + getType().toString());
	}

	/**
	 * clean up after linking objects together by deleting the openGL-shaderObject
	 */
	public void delete() {
		if (!compiled) {
			return;
		}
		compiled = false;
		glDeleteShader(shaderID);
		GLErrors.checkForError(TAG, "glDeleteShader");
	}



	/**
	 * Print source code of shader object line by line with leading line
	 * numbers and in-place error messages
	 *
	 * @param log
	 *              the shader log info.
	 */
	private void printDebug(final String log, final String source, final String sourceName) {
		Log.error(TAG, "Errors in \"" + sourceName + "\":\n" + log);

		// stores line number and error message
		final LinkedHashMap<Integer, ArrayList<String>> errorList = new LinkedHashMap<>();

		// regular expression for extracting error line and message for nearly all devices
		final String generalExp = "(?:ERROR: )?\\d+:\\(?(\\d+)\\)?: (.+)";
		final Pattern regExPattern = Pattern.compile(generalExp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		try(Scanner scannerShaderLog = new Scanner(log)) {
			while(scannerShaderLog.hasNextLine()) {
				extractErrors(scannerShaderLog.nextLine(), regExPattern, errorList);
			}
		}

		// format source code
		try(Scanner scannerSourceCode = new Scanner(source)) {
			int lineNumber = 1;

			while(scannerSourceCode.hasNextLine()) {
				printFormattedLine(scannerSourceCode.nextLine(),
						lineNumber, errorList.get(lineNumber));

				lineNumber++;
			}
		}
		throw new RuntimeException("Non-compilable shader code!");
	}

	/**
	 * Getter for the source path of a shader
	 * @return the shaders source path
	 */
	public String getSourcePath() {
		return sourcePath;
	}
	
	private static final String ERROR_NO = "    ";
	private static final String ERROR_YES = "\\->>>>>>";
	private static final DecimalFormat LINE_NUMBER_FORMAT = new DecimalFormat("0000");

	/**
	 * print a line of code together with its errors
	 * @param codeLine the line to display
	 * @param lineNumber the number of the line
	 * @param errorList
	 */
	private void printFormattedLine(String codeLine, int lineNumber, ArrayList<String> errorList) {
		final String formattedLineNumber = LINE_NUMBER_FORMAT.format(lineNumber);
		final String annotatedLine = formattedLineNumber + ERROR_NO + ": " + codeLine;
		Log.log(null, annotatedLine);
		if (errorList != null) {
			for (final String string : errorList) {
				Log.log(null, ERROR_YES + ": " + string);
			}
		}
	}

	/**
	 * parse a given logLine and insert an Error line into the given collection
	 * @param logLine the log line to analyze
	 * @param regExPattern a pattern determining whether a line contains an error
	 * @param errorList the Error line collection to enlarge
	 */
	private void extractErrors(String logLine, Pattern regExPattern, LinkedHashMap<Integer, ArrayList<String>> errorList) {
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
