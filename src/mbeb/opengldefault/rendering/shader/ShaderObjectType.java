package mbeb.opengldefault.rendering.shader;

import java.util.Arrays;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;

/**
 * All the Kinds of shaders in existence, and associated file extensions
 */
public enum ShaderObjectType {
	VERTEX(GL_VERTEX_SHADER, "vert"),
	FRAGMENT(GL_FRAGMENT_SHADER, "frag"),
	GEOMETRY(GL_GEOMETRY_SHADER, "geom"),
	TCS(GL_TESS_CONTROL_SHADER, "tcs"),
	TES(GL_TESS_EVALUATION_SHADER, "tes");

	private final int glType;
	private final String[] extensions;

	ShaderObjectType(int glType, String... extensions) {
		this.glType = glType;
		this.extensions = extensions;
	}

	public int getGlType() {
		return glType;
	}

	public boolean usesExtension(String testExtension) {
		return Arrays.stream(extensions).anyMatch(myExtension -> myExtension.equalsIgnoreCase(testExtension));
	}

	/**
	 * @param extension
	 * @return the Type associated with the given extension, or null if none matches
	 */
	public static ShaderObjectType byExtension(String extension) {
		return Arrays.stream(values()).filter(type -> type.usesExtension(extension)).findFirst().orElse(null);
	}


	/**
	 * @param sourcePath
	 * @return the Type associated with the given extension of a fileName, or null if none matches
	 */
	public static ShaderObjectType byName(String sourcePath) {
		String[] data = sourcePath.split("\\.");
		String extension = data[data.length - 1];
		return byExtension(extension);
	}
}
