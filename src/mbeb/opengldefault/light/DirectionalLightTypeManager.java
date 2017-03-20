package mbeb.opengldefault.light;

import mbeb.opengldefault.rendering.shader.*;

/**
 * I'm managing directional lights. (like a sun that is far far away so her rays are [almost] parallel)
 *
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public class DirectionalLightTypeManager extends LightTypeManager {

	/**
	 * creates a Manager of DirectionalLights with their Parameters
	 */
	public DirectionalLightTypeManager() {
		super("DIRECTIONAL_LIGHT_CAPACITY", 2, UBOManager.DIRECTIONALLIGHT, 1);
	}

}
