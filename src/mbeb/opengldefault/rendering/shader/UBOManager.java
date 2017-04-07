package mbeb.opengldefault.rendering.shader;

import mbeb.opengldefault.gl.buffer.UniformBuffer;

public abstract class UBOManager {

	public static final UniformBuffer MATRICES = new UniformBuffer(0, "Matrices");
	public static final UniformBuffer DIRECTIONALLIGHT = new UniformBuffer(1, "DirectionalLightBlock");
	public static final UniformBuffer POINTLIGHT = new UniformBuffer(2, "PointLightBlock");
	public static final UniformBuffer SPOTLIGHT = new UniformBuffer(3, "SpotLightBlock");


	private UBOManager() {
		//should never be instantiated
	}
}
