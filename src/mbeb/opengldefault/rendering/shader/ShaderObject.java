package mbeb.opengldefault.rendering.shader;

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
	private int shaderID;
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
	 * attaches this shader to an openGL shader program
	 * @param shaderProgramHandle to program to attach to
	 */
	public void attachShader(int shaderProgramHandle) {
		glAttachShader(shaderProgramHandle, getCompiledShaderID());
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
				final String formattedLineNumber = numberFormat.format(lineNumber);
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

}
