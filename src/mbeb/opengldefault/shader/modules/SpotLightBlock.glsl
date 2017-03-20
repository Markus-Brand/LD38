layout (std140) uniform SpotLightBlock{
    int numSpotLights;
    
    SpotLight[
	#if !defined(SPOT_LIGHT_CAPACITY) || SPOT_LIGHT_CAPACITY == 0
	1
	#else
	SPOT_LIGHT_CAPACITY
	#endif
	] spotLights;
};