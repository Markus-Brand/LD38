package mbeb.opengldefault.light;

import mbeb.opengldefault.rendering.shader.*;

public class SpotLightManager extends LightTypeManager {

	public SpotLightManager() {
		this.shaderLightTypeParameterName = "SPOT_LIGHT_CAPACITY";
		this.lightBlockSize = 4;
		this.UBOBaseName = UBOManager.SPOTLIGHT;
		this.lightCapacity = 4;
		init();
	}
}
