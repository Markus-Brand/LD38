package mbeb.opengldefault.gl.shader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import mbeb.opengldefault.logging.*;

/**
 * Parse the source code for one {@link ShaderObject}
 *
 * @author Erik
 */
public class ShaderPreprocessor {

	private static final String TAG = "ShaderPreprocessor";
	public static final String GLSL_VERSION = "330 core";
	private static final String INCLUDE_TAG = "#include ";

	/** all the parameters that are pasted into the header of each shader as define statements */
	private final Map<String, String> parameters;
	/** cached version of the header string for each shader */
	private String header;
	/** whether the paramters of the header have changed and it has to be re-created */
	private boolean parametersDirty;

	/**
	 * construct a new preprocessor with a given set of parameters
	 * @param parameters can be null
	 */
	public ShaderPreprocessor(Map<String, String> parameters) {
		this.parameters = parameters != null ? parameters : new HashMap<>();
		setParametersDirty(true);
	}

	/**
	 * getter for parameters
	 * @param name the name of the parameter
	 * @return the value of the parameter, or an empty string
	 */
	public String getParameter(final String name) {
		final String obj = parameters.get(name);
		return obj == null ? "" : obj;
	}

	public void updateParameter(final String name, final String value) {
		if (Objects.equals(value, parameters.get(name))) {
			//no change in object
			return;
		}
		parameters.put(name, value);
		setParametersDirty(true);
	}

	/**
	 * @return true, if the cached header String is outdated
	 */
	public boolean areParametersDirty() {
		return parametersDirty;
	}

	public void setParametersDirty(boolean parametersDirty) {
		this.parametersDirty = parametersDirty;
	}

	/**
	 * processes the file and adds the parameter-header
	 *
	 * @param fileName
	 * @return
	 */
	public String getProcessedShaderFile(String fileName) {
		return getHeader() + getProcessedCode(fileName);
	}

	/**
	 * processes the file, but dont put any header
	 *
	 * @param fileName
	 * @return
	 */
	private String getProcessedCode(String fileName) {
		String processedContent;
		if (Cache.getInstance().hasProcessedVersionCached(fileName)) {
			processedContent = Cache.getInstance().getProcessedContent(fileName);
		} else {
			processedContent = process(fileName);
			Cache.getInstance().saveProcessedContent(fileName, processedContent);
		}
		return processedContent;
	}

	/**
	 * @return the currently valid serialized form of the parameters
	 */
	private String getHeader() {
		if (areParametersDirty()) {
			setParametersDirty(false);
			StringBuilder headerBuilder = new StringBuilder("#version ").append(GLSL_VERSION).append(System.getProperty("line.separator"));
			for (Map.Entry<String, String> parameter : parameters.entrySet()) {
				final Object value = parameter.getValue() != null ? parameter.getValue() : "";
				headerBuilder.append("#define ").append(parameter.getKey()).append(" ").append(value).append(System.getProperty("line.separator"));
			}
			header = headerBuilder.toString();
		}
		return header;
	}

	/**
	 * actually process a File
	 *
	 * @param fileName
	 * @return the inflated content of this file
	 */
	private String process(String fileName) {
		String rawSource = Cache.getInstance().getRawSource(fileName);

		return Arrays.stream(rawSource.split("\\R")).map((String line) -> {
			String trimmedLine = line.trim();
			if (trimmedLine.startsWith(INCLUDE_TAG)) {
				String includedFileName = trimmedLine.substring(INCLUDE_TAG.length()).trim();
				return getProcessedCode(includedFileName);
			}
			return line;
		}).collect(Collectors.joining("\n"));
	}

	private static final class Cache {

		private static Cache instance = null;

		private final Map<String, String> rawContent;
		private final Map<String, String> processedContent;

		private Cache() {
			rawContent = new HashMap<>();
			processedContent = new HashMap<>();
		}

		public static Cache getInstance() {
			if (instance == null) {
				instance = new Cache();
			}
			return instance;
		}

		public String getRawSource(String fileName) {
			String content = rawContent.get(fileName);
			if (content == null) {
				content = loadSource(fileName);
				rawContent.put(fileName, content);
			}
			return content;
		}

		public String getProcessedContent(String fileName) {
			return processedContent.get(fileName);
		}

		public boolean hasProcessedVersionCached(String fileName) {
			return getProcessedContent(fileName) != null;
		}

		public void saveProcessedContent(String fileName, String content) {
			processedContent.put(fileName, content);
		}

		/**
		 * returns the Shader Source of a given path
		 *
		 * @param path the path of the shader to load
		 * @return the source of the loaded shader
		 */
		private static String loadSource(final String path) {
			try {
				InputStream shaderURL = ShaderPreprocessor.class.getResourceAsStream("/shaders/" + path);
				try(Scanner sc = new Scanner(shaderURL, "UTF-8")) {
					return sc.useDelimiter("\\A").next();
				}
			} catch(final Exception ex) {
				Log.error(TAG, "Loading shader source failed:" + path + "\n", ex);
				return "";
			}
		}
	}

}
