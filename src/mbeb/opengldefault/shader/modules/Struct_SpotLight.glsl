//size 80
struct SpotLight{
	vec3 position;
	vec3 direction;
	vec3 color;

	float cutoff;
	float outerCutoff;

	float constant;
	float linear;
	float quadratic;
};