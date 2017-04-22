
in vec2 frag_in_tex;
in vec3 frag_in_pos;
in vec3 frag_in_norm;
in mat3 frag_in_tbn; //tangent-bitangent-normal (needed for normal mapping)

out vec4 color;

#include modules/Struct_DirLight.glsl
#include modules/Struct_PointLight.glsl
#include modules/Struct_SpotLight.glsl

float ambientStrength = 0.4;
float specularStrength = 1.0;

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

vec3 calcNormal(const in vec3 normalIn, const in vec3 fragPos){
	return normalize(normalIn + 
		vec3(0.05 * cos(fragPos.x * 120.0 +  0.80 * time), 0.05 *  sin(fragPos.y * fragPos.z * 130.0 + 0.7 * time), 0.05 *  sin(fragPos.z * 140.0 + 0.5 * time)) +
		vec3(0.02 * cos(fragPos.x * fragPos.y * fragPos.z * 524.0 +  1.3 * time), 0.02 *  sin(fragPos.y * 734.0 + 3.4 * time), 0.02 *  sin(fragPos.z * 256.0 + 2.07 * time)) +
		vec3(0.01 * cos(fragPos.x * 5024.0 +  5.3 * time), 0.01 *  sin(fragPos.y * 7304.0 + 6.4 * time), 0.01 *  sin(fragPos.z * fragPos.y* 2056.0 + 7.0 * time)));
}

void main(){
	vec3 normal = frag_in_norm;

	vec2 yflip = vec2(frag_in_tex.x, 1.0 - frag_in_tex.y);

	vec4 diffuseColorAlpha = vec4(0, 0, 1, 1);
	vec3 diffuseColor = diffuseColorAlpha.rgb;
	float materialAlpha = diffuseColorAlpha.a;
	vec3 specularColor = vec3(0.1, 0.1, 0.1);
	normal = calcNormal(normal, frag_in_pos);
	int shininess = 16;

	vec3 viewDir = normalize(viewPos - frag_in_pos);

	float reflectivity =  pow(dot(viewDir, normal), 1);
	reflectivity = clamp(reflectivity, 0.1, 0.9);
	
	vec3 I = -viewDir;
	vec3 R = reflect(I, normalize(normal));
	
	diffuseColor = mix(texture(skybox, R).xyz, diffuseColor, reflectivity);


	vec3 result = vec3(0);

    //apply lights
	for(int i = 0; i < numPointLights; i++){
		result += calcPointLight(pointLights[i], normal, frag_in_pos, viewDir, diffuseColor, specularColor, shininess);
	}
	for(int i = 0; i < numDirectionalLights; i++){
		result += calcDirectionalLight(directionalLights[i], normal, frag_in_pos, viewDir, diffuseColor, specularColor, shininess);
	}
	for(int i = 0; i < numSpotLights; i++){
		result += calcSpotLight(spotLights[i], normal, frag_in_pos, viewDir, diffuseColor, specularColor, shininess);
	}

    //ambient lighting
	vec3 ambient = ambientStrength * diffuseColor;
	result += ambient;


#ifdef GAMMA_CORRECTION
	float gamma = 2.2;
	float gammaInverse = 1.0 / gamma;
	result.x = pow(result.x, gammaInverse);
	result.y = pow(result.y, gammaInverse);
	result.z = pow(result.z, gammaInverse);
#endif

	vec4 texColor = vec4(result, materialAlpha);
	if(texColor.a > 0.01){
		color = texColor;
	}else{
		discard;
	}
}