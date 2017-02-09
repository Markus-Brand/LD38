in vec2 tex;
in vec3 pos;
in vec3 normal;

#define SHININESS 64

//size 48
struct PointLight{
	vec3 position;

	vec3 color;

	float constant;
	float linear;
	float quadratic;
};

out vec4 color;

uniform sampler2D u_texture;

float ambientStrength = 0.1f;
float specularStrength = 2.5f;
float reflectionStrength = 0.4f;

uniform vec3 viewPos;

vec3 calcPointLight(PointLight pl, vec3 norm, vec3 viewDir);

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

	result += calcPointLight(light, norm, viewDir);
	
	vec3 ambient = ambientStrength * vec3(texture(u_texture, tex));

	result += ambient;
	
	vec4 texColor = vec4(result, 1);
	color = texColor;
}

vec3 calcPointLight(PointLight light, vec3 norm, vec3 viewDir){
	vec3 direction = light.position - pos;

	float distance = length(direction);
	direction = normalize(direction);

	float diff = max(dot(norm, direction), 0.0);
	vec3 diffuse = diff * light.color;

	vec3 halfwayDir = normalize(direction + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0f), SHININESS);
	vec3 specular = specularStrength * spec * light.color;
	
	float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * distance * distance);

	diffuse  *= attenuation;
	specular *= attenuation;  

	return vec3(texture(u_texture, tex)) * diffuse + vec3(texture(u_texture, tex)) * specular;
}
