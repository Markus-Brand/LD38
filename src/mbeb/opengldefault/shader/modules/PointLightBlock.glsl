layout (std140) uniform PointLightBlock{
    int numPointLights;
    
    PointLight[
	#if !defined(POINT_LIGHT_CAPACITY) || POINT_LIGHT_CAPACITY == 0
	1
	#else
	POINT_LIGHT_CAPACITY
	#endif
	] pointLights;
};