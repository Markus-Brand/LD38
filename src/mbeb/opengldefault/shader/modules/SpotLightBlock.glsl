layout (std140) uniform SpotLightBlock{
    int numSpotLights;
    
    SpotLight[
	#if SPOT_LIGHT_CAPACITY == 0
	1
	#else
	SPOT_LIGHT_CAPACITY
	#endif
	] spotLights;
};