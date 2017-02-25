package mbeb.opengldefault.rendering.shader;

import java.util.*;

public class UBOManager {
	private static UBOManager instance;
	public static String MATRICES = "Matrices";
	public static String DIRECTIONALLIGHT = "DirectionalLightBlock";
	public static String POINTLIGHT = "PointLightBlock";
	public static String SPOTLIGHT = "SpotLightBlock";

	private final Map<String, Integer> UBOMapping;

	private UBOManager() {
		UBOMapping = new HashMap<String, Integer>();
		UBOMapping.put(MATRICES, 0);
		UBOMapping.put(DIRECTIONALLIGHT, 1);
		UBOMapping.put(POINTLIGHT, 2);
		UBOMapping.put(SPOTLIGHT, 3);
	}

	public static UBOManager getInstance() {
		if (instance == null) {
			instance = new UBOManager();
		}
		return instance;
	}

	public int getUBOID(final String UBOName) {
		return UBOMapping.get(UBOName);
	}
}
