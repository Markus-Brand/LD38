package mbeb.opengldefault.light;

import mbeb.opengldefault.rendering.shader.*;

/**
 * I'm managing point lights. (like a lamp without shade or a near sun)
 *
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public class PointLightTypeManager extends LightTypeManager {

	/**
	 * creates a Manager of PointLights with their Parameters
	 */
	public PointLightTypeManager() {
		super("POINT_LIGHT_CAPACITY", 3, UBOManager.POINTLIGHT, 8);
	}
}
