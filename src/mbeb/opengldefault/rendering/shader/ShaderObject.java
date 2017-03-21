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

	private final ShaderObjectType type;
	private final String sourcePath;
	private final ShaderPreprocessor preprocessor;
	private int shaderID;
	private boolean compiled;

	public ShaderObject(String sourcePath, ShaderPreprocessor preprocessor) {
		this.sourcePath = sourcePath;
		this.preprocessor = preprocessor;
		this.type = ShaderObjectType.byName(sourcePath);
		compiled = false;
	}

	public ShaderObjectType getType() {
		return type;
	}

	/**
	 * ensures that this shader is compiled
	 * @return a shader object
	 */
	public int getCompiledShaderID() {
		if (!compiled) {
			final String sourceString = preprocessor.getProcessedShaderFile(sourcePath);
			shaderID = glCreateShader(getType().getGlType());
			GLErrors.checkForError(TAG, "glCreateShader");
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
	 *
	 * @return a shader object
	 */
	public int forceRecompile() {
		delete();
		return getCompiledShaderID();
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
