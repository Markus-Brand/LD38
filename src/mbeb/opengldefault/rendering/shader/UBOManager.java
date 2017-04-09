package mbeb.opengldefault.rendering.shader;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gl.buffer.UniformBuffer;

public abstract class UBOManager {

	public static final UniformBuffer MATRICES = new UniformBuffer(
			0, "Matrices", Constants.MAT4_SIZE * 4);
	public static final UniformBuffer DIRECTIONALLIGHT = new UniformBuffer(
			1, "DirectionalLightBlock", Constants.BLOCK_SIZE);
	public static final UniformBuffer POINTLIGHT = new UniformBuffer(
			2, "PointLightBlock", Constants.BLOCK_SIZE);
	public static final UniformBuffer SPOTLIGHT = new UniformBuffer(
			3, "SpotLightBlock", Constants.BLOCK_SIZE);


	private UBOManager() {
		//should never be instantiated
	}
}
