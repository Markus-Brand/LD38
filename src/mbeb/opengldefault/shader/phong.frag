in vec2 tex;
in vec3 pos;
in vec3 normal;

#define SHININESS 16

out vec4 color;

uniform sampler2D u_texture;

float ambientStrength = 0.1f;
float specularStrength = 2.5f;
float reflectionStrength = 0.4f;

uniform vec3 viewPos;

#include modules/PointLight.glsl
vec3 calcPointLight(PointLight light, vec3 norm, vec3 viewDir, vec3 fragmentPos);


void main(){ 
	vec3 norm = normalize(normal);	
	
	vec3 viewDir = normalize(viewPos - pos);

	vec3 result = vec3(0); 
	
	PointLight light;
	light.position = vec3(2, 0, 3);
	light.color = vec3(1, 1, 1);
	light.constant = 1;
	light.linear = 0;
	light.quadratic = 0;

	result += calcPointLight(light, norm, viewDir, pos, specularStrength);
	
	vec3 ambient = ambientStrength * vec3(texture(u_texture, tex));

	result += ambient;
	
	vec4 texColor = vec4(result, 1);
	color = texColor;
}
