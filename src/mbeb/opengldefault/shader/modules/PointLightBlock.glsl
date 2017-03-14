layout (std140) uniform PointLightBlock{
    int numPointLights;
    
    PointLight[
	#if POINT_LIGHT_CAPACITY == 0
	1
	#else
	POINT_LIGHT_CAPACITY
	#endif
	] pointLights;
};