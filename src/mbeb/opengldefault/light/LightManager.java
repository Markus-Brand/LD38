package mbeb.opengldefault.light;

import java.util.*;

import mbeb.opengldefault.gl.shader.*;

/**
 * I am managing different lights (like directional, point or spot lights), e.g. adding, deleting and updating their
 * data on the graphics card.
 * I'm also storing shaders witch are using them.
 *
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public class LightManager {
	/** Class Name Tag */
	private static final String TAG = "LightManager";

	/** LightTypeManager for DirectionalLights */
	private final LightTypeManager directionalLightTypeManager;
	/** LightTypeManager for PointLights */
	private final LightTypeManager pointLightTypeManager;
	/** LightTypeManager for SpotLights */
	private final LightTypeManager spotLightTypeManager;
	/** List of shaders (witch normally use Light) */
	private final ArrayList<ShaderProgram> shaders;

	/**
	 * creates LightManager with 3 LightTypeManagers for each LightType (Directional, Point, Spot) and stores the light
	 * using shaders
	 * shaders
	 */
	public LightManager() {
		directionalLightTypeManager = new DirectionalLightTypeManager();
		pointLightTypeManager = new PointLightTypeManager();
		spotLightTypeManager = new SpotLightTypeManager();
		shaders = new ArrayList<>();
	}

	/**
	 * updates the <i>TYPE</i>_LIGHT_CAPACITY Parameters of all registered shaders
	 */
	private void updateShaders() {
		shaders.forEach(this::updateShader);
	}

	public void rewriteUBO() {
		directionalLightTypeManager.resizeBuffer();
		pointLightTypeManager.resizeBuffer();
		spotLightTypeManager.resizeBuffer();
	}

	/**
	 * @param shader
	 *            the shader witch should be registered
	 */
	public void addShader(final ShaderProgram shader) {
		shaders.add(shader);
		updateShader(shader);
	}

	/**
	 * @param shader
	 *            updates the <i>TYPE</i>_LIGHT_CAPACITY Parameters of this shader
	 */
	private void updateShader(final ShaderProgram shader) {
		directionalLightTypeManager.updateShader(shader);
		pointLightTypeManager.updateShader(shader);
		spotLightTypeManager.updateShader(shader);
	}

	/**
	 * @param shader
	 *            that will be unregistered
	 */
	public void removeShader(final ShaderProgram shader) {
		shaders.remove(shader);
	}

	/**
	 * adds DirectionalLight <i>light</i> and updates all shaders
	 *
	 * @param light
	 *            that will be added
	 */
	public void addLight(final DirectionalLight light) {
		directionalLightTypeManager.addLight(light);
		updateShaders();
	}

	/**
	 * adds PointLight <i>light</i> and updates all shaders
	 *
	 * @param light
	 *            that will be added
	 */
	public void addLight(final PointLight light) {
		pointLightTypeManager.addLight(light);
		updateShaders();
	}

	/**
	 * adds SpotLight <i>light</i> and updates all shaders
	 *
	 * @param light
	 *            that will be added
	 */
	public void addLight(final SpotLight light) {
		spotLightTypeManager.addLight(light);
		updateShaders();
	}

	/**
	 * checks if Lights were changed since last update and refreshes shader data
	 *
	 * @param deltaTime
	 */
	public void update(final double deltaTime) {
		directionalLightTypeManager.update(deltaTime);
		pointLightTypeManager.update(deltaTime);
		spotLightTypeManager.update(deltaTime);
	}
}
