/**
 * utility attenuation function that can be included anywhere
 */
#ifndef MODULE_ATTENUATION
	#define MODULE_ATTENUATION
	
	float calculateAttenuation(const in float value, const in float constant, const in float linear, const in float quadreatic) {
		return 1.0f / (constant + linear * value + quadreatic * value * value);
	}
	
#endif