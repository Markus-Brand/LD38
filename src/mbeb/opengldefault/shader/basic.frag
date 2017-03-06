#define SHININESS 64

in vec2 tex;
in vec3 pos;
in vec3 normal;

out vec4 color;

uniform sampler2D u_texture;

#include modules/Struct_DirLight.glsl
#include modules/Struct_PointLight.glsl
#include modules/Struct_SpotLight.glsl

float ambientStrength = 0.04f;
float specularStrength = 2.5f;
float reflectionStrength = 0.4f;

layout (std140) uniform DirectionalLightBlock{	
    int numDirectionalLights;
    
    DirectionalLight[
	#if DIRECTIONAL_LIGHT_CAPACITY == 0
	1
	#else
	DIRECTIONAL_LIGHT_CAPACITY
	#endif
	] directionalLights;
};

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

uniform int alpha;

uniform vec3 viewPos;

uniform samplerCube skybox;

uniform float time;

uniform int water;

vec3 calcDirectionalLight(DirectionalLight light, vec3 norm, vec3 viewDir);
vec3 calcPointLight(PointLight light, vec3 norm, vec3 viewDir);
vec3 calcSpotLight(SpotLight light, vec3 norm, vec3 viewDir);

vec3 calcNormal(vec3 normalIn, vec3 fragPos){
	return normalize(normalIn) + vec3(0.03 * cos(fragPos.x * 120.0f +  8.0f * time), 0.03 *  sin(fragPos.y * 130.0f + 7.0f * time), 0.03 *  sin(fragPos.z * 140.0f + 5.0f * time));
}

void main(){ 
	vec3 norm;
	if(water == 0){	
		norm = normalize(calcNormal(normal, pos));		
	}else{
		norm = normalize(normal);		
	}
	
	vec3 viewDir = normalize(viewPos - pos);

	if(dot(norm, viewDir) < 0){
		//norm = -norm;
	}
	
	vec3 result = vec3(0); 
	
	for(int i = 0; i < numPointLights; i++){
		result += calcPointLight(pointLights[i], norm, viewDir);
	}
	
	for(int i = 0; i < numDirectionalLights; i++){
		result += calcDirectionalLight(directionalLights[i], norm, viewDir);
	}
	
	for(int i = 0; i < numSpotLights; i++){
		result += calcSpotLight(spotLights[i], norm, viewDir);
	}
	
	vec3 ambient = ambientStrength * vec3(texture(u_texture, tex));

	result += ambient;

	float gamma = 2.0;
   	result = pow(result, vec3(1.0/gamma));
    
	float a = texture(u_texture, tex).r * reflectionStrength;
	if(a > 0){
		vec3 I = normalize(pos - viewPos);
		vec3 R = reflect(I, normalize(norm));
		vec3 reflectionColor = vec3(texture(skybox, R));
	
		result = mix(result, reflectionColor, a);	
	}
	
	if(alpha == 0){
		color = vec4(result.x, result.y, result.z, 1.0f);
	}else if(alpha == 1){
		vec4 texColor = vec4(result.x, result.y, result.z, (texture(u_texture, tex)).a);
		if(texColor.a > 0.01){	
			color = texColor;
		}else{	
			discard;
		}
	}	
    /*
	if(gl_FragCoord.x > 960){
		float gamma = 1.2;
   		color.xyz = pow(color.xyz, vec3(1.0/gamma));
	}
	*/
}

vec3 calcDirectionalLight(DirectionalLight light, vec3 norm, vec3 viewDir){	
	vec3 direction = normalize(-light.direction);

	float diff = max(dot(norm, direction), 0.0);
	vec3 diffuse = diff * light.color;

	vec3 halfwayDir = normalize(direction + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0f), SHININESS);
	vec3 specular = specularStrength * spec * light.color;

	return vec3(texture(u_texture, tex)) * diffuse + vec3(texture(u_texture, tex)) * specular;
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

vec3 calcSpotLight(SpotLight light, vec3 norm, vec3 viewDir){

	vec3 direction = light.position - pos;

	float distance = length(direction);
	direction = normalize(direction);	

	float diff = max(dot(norm, direction), 0.0);
	vec3 diffuse = diff * light.color;

	vec3 halfwayDir = normalize(direction + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0f), SHININESS);
	vec3 specular = specularStrength * spec * light.color;
	
	float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * distance * distance);
	
	float intensity = clamp((dot(direction, normalize(-light.direction)) - light.outerCutoff) / (light.cutoff - light.outerCutoff), 0.0f, 1.0f);

	diffuse *= intensity;
	specular *= intensity * 2;

	diffuse  *= attenuation;
	specular *= attenuation;  

	return vec3(texture(u_texture, tex)) * (diffuse) + vec3(texture(u_texture, tex)) * specular;
}