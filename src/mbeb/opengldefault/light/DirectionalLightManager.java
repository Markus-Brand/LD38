package mbeb.opengldefault.light;

import mbeb.opengldefault.rendering.shader.*;

public class DirectionalLightManager extends LightTypeManager {

	public DirectionalLightManager() {
		this.shaderLightTypeParameterName = "DIRECTIONAL_LIGHT_CAPACITY";
		this.lightBlockSize = 2;
		this.UBOBaseName = UBOManager.DIRECTIONALLIGHT;
		this.lightCapacity = 1;
		init();
	}

}
