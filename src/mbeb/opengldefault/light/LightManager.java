package mbeb.opengldefault.light;

import java.util.*;

import mbeb.opengldefault.rendering.shader.*;

/**
 * @author Erik + Merlin + Markus :)
 */
public class LightManager {
	/** Class Name Tag */
	private static final String TAG = "LightManager";

	/** LightTypeManager for DirectionalLights */
	private final LightTypeManager directionalLightManager;
	/** LightTypeManager for PointLights */
	private final LightTypeManager pointLightManager;
	/** LightTypeManager for SpotLights */
	private final LightTypeManager spotLightManager;
	/** List of shaders (witch normally use Light) */
	private final ArrayList<Shader> shaders;

	/**
	 * creates LightManager with 3 LightTypeManagers for each LightType (Directional, Point, Spot) and stores the light using shaders
	 * shaders
	 */
	public LightManager() {
		directionalLightManager = new DirectionalLightManager();
		pointLightManager = new PointLightManager();
		spotLightManager = new SpotLightManager();
		shaders = new ArrayList<>();
	}

	/**
	 * updates the <i>TYPE</i>_LIGHT_CAPACITY Parameters of all registered shaders
	 */
	private void updateShaders() {
		shaders.forEach((final Shader shader) -> {
			updateShader(shader);
		});
	}

	/**
	 * @param shader
	 *            the shader witch should be registered
	 */
	public void addShader(final Shader shader) {
		shaders.add(shader);
		updateShader(shader);
	}

	/**
	 * @param shader
	 *            updates the <i>TYPE</i>_LIGHT_CAPACITY Parameters of this shader
	 */
	private void updateShader(final Shader shader) {
		directionalLightManager.updateShader(shader);
		pointLightManager.updateShader(shader);
		spotLightManager.updateShader(shader);
	}

	private void removeShader(final Shader shader) {
		shaders.remove(shader);
	}

	public void addLight(final DirectionalLight light) {
		directionalLightManager.addLight(light);
		updateShaders();
	}

	public void addLight(final PointLight light) {
		pointLightManager.addLight(light);
		updateShaders();
	}

	public void addLight(final SpotLight light) {
		spotLightManager.addLight(light);
		updateShaders();
	}

	/**
	 * checks if Lights were changed since last update and refreshes shader data
	 *
	 * @param deltaTime
	 */
	public void update(final double deltaTime) {
		directionalLightManager.update(deltaTime);
		pointLightManager.update(deltaTime);
		spotLightManager.update(deltaTime);
	}
}
