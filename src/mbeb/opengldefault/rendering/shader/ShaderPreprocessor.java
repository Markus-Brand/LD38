package mbeb.opengldefault.rendering.shader;

import java.io.*;
import java.util.*;

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

	private final Map<String, Object> parameters;
	private String header;
	private boolean parametersDirty;

	public ShaderPreprocessor(Map<String, Object> parameters) {
		this.parameters = parameters != null ? parameters : new HashMap<>();
		setParametersDirty(true);
	}

	public String getParameter(final String name) {
		final Object obj = parameters.get(name);
		return obj == null ? "" : obj.toString();
	}

	public void updateParameter(final String name, final Object value) {
		if (Objects.equals(value, parameters.get(name))) {
			//no change in object
			return;
		}
		parameters.put(name, value);
		setParametersDirty(true);
	}

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
			for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
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
	 * @return
	 */
	private String process(String fileName) {
		String rawSource = Cache.getInstance().getRawSource(fileName);

		String[] sourceParts = Arrays.stream(rawSource.split("\\R")).map((String line) -> {
			String trimmedLine = line.trim();
			if (trimmedLine.startsWith(INCLUDE_TAG)) {
				String includedFileName = trimmedLine.substring(INCLUDE_TAG.length()).trim();
				return getProcessedCode(includedFileName);
			}
			return line;
		}).map((String line) -> line + "\n").toArray(String[]::new);

		StringBuilder result = new StringBuilder();
		for (String sourcePart : sourceParts) {
			result.append(sourcePart);
		}
		return result.toString();
	}

	private static final class Cache {

		private static Cache instance = null;

		public static Cache getInstance() {
			if (instance == null) {
				instance = new Cache();
			}
			return instance;
		}

		////
		private final Map<String, String> rawContent;
		private final Map<String, String> processedContent;

		private Cache() {
			rawContent = new HashMap<>();
			processedContent = new HashMap<>();
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
		 * @param path
		 * @return
		 */
		private static String loadSource(final String path) {
			try {
				InputStream shaderURL = ShaderPreprocessor.class.getResourceAsStream("/mbeb/opengldefault/shader/" + path);
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
