package mbeb.opengldefault.light;

import mbeb.opengldefault.gl.shader.*;

/**
 * I'm managing spotlights. (like a lamp with shade or a flashlight, trafficlight, headlight, floodlight or even a spotlight :)
 *
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public class SpotLightTypeManager extends LightTypeManager {

	/**
	 * creates a Manager of SpotLights with their Parameters
	 */
	public SpotLightTypeManager() {
		super("SPOT_LIGHT_CAPACITY", 4, UBOManager.SPOTLIGHT, 4);
	}
}
