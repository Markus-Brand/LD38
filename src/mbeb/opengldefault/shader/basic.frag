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

#include modules/DirectionalLightBlock.glsl
#include modules/PointLightBlock.glsl
#include modules/SpotLightBlock.glsl

uniform int alpha;

uniform vec3 viewPos;

uniform samplerCube skybox;

uniform float time;

uniform int water;

#include modules/DirectionalLightLogic.glsl
#include modules/PointLightLogic.glsl
#include modules/SpotLightLogic.glsl


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
}