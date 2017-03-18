layout (std140) uniform DirectionalLightBlock{
    int numDirectionalLights;
    
    DirectionalLight[
	#if !defined(DIRECTIONAL_LIGHT_CAPACITY) || DIRECTIONAL_LIGHT_CAPACITY == 0
	1
	#else
	DIRECTIONAL_LIGHT_CAPACITY
	#endif
	] directionalLights;
};