package mbeb.opengldefault.rendering.shader;

import java.util.*;

public abstract class UBOManager {

	public static final String MATRICES = "Matrices";
	public static final String DIRECTIONALLIGHT = "DirectionalLightBlock";
	public static final String POINTLIGHT = "PointLightBlock";
	public static final String SPOTLIGHT = "SpotLightBlock";

	private static final Map<String, Integer> UBOMapping;

	static {
		UBOMapping = new HashMap<>();
		UBOMapping.put(MATRICES, 0);
		UBOMapping.put(DIRECTIONALLIGHT, 1);
		UBOMapping.put(POINTLIGHT, 2);
		UBOMapping.put(SPOTLIGHT, 3);
	}

	public static int getUBOID(final String UBOName) {
		return UBOMapping.get(UBOName);
	}
}
