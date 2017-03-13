package mbeb.opengldefault.light;

import mbeb.opengldefault.rendering.shader.*;

public class PointLightManager extends LightTypeManager {

	public PointLightManager() {
		this.shaderLightTypeParameterName = "POINT_LIGHT_CAPACITY";
		this.lightBlockSize = 3;
		this.UBOBaseName = UBOManager.POINTLIGHT;
		this.lightCapacity = 8;
		init();
	}
}
