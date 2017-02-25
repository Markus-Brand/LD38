/**
 * utility attenuation function that can be included anywhere
 */
#ifndef MODULE_ATTENUATION
	#define MODULE_ATTENUATION
	
	float calculateAttenuation(float value, float constant, float linear, float quadreatic) {
		return 1.0f / (constant + linear * value + quadreatic * value * value);
	}
	
#endif