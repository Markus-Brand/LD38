
in vec2 tex;
in vec3 pos;
in vec3 normal;
in mat3 tbn; //tangent-bitangent-normal (needed for normal mapping)

out vec4 color;

#include modules/MaterialUniform.glsl

#include modules/Struct_DirLight.glsl
#include modules/Struct_PointLight.glsl
#include modules/Struct_SpotLight.glsl

float ambientStrength = 0.1;
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


void main(){
	vec3 norm = normal;

	vec4 diffuseColorAlpha = materialDiffuseAlpha(tex);
	vec3 diffuseColor = diffuseColorAlpha.rgb;
	float materialAlpha = diffuseColorAlpha.a;
	vec3 specularColor = materialSpecular(tex);
	vec3 emissionColor = materialEmit(tex);
	vec3 normalFromMap = materialNormal(tex);
	int shininess = materialShininess();

	vec3 viewDir = normalize(viewPos - pos);

	//normal mapping
	if (length(normalFromMap) > 0.01) {
		normalFromMap = normalize(normalFromMap * 2.0 - 1.0);
		norm = normalize(tbn * normalFromMap);
	}


	vec3 result = vec3(0);

    //apply lights
	for(int i = 0; i < numPointLights; i++){
		result += calcPointLight(pointLights[i], norm, viewDir, diffuseColor, specularColor, shininess);
	}
	for(int i = 0; i < numDirectionalLights; i++){
		result += calcDirectionalLight(directionalLights[i], norm, viewDir, diffuseColor, specularColor, shininess);
	}
	for(int i = 0; i < numSpotLights; i++){
		result += calcSpotLight(spotLights[i], norm, viewDir, diffuseColor, specularColor, shininess);
	}

    //ambient lighting
	vec3 ambient = ambientStrength * diffuseColor;
	result += ambient;

	//emission
	result += emissionColor;


#ifdef GAMMA_CORRECTION
	float gamma = 2.2;
	float gi = 1.0 / gamma;
	result.x = pow(result.x, gi);
	result.y = pow(result.y, gi);
	result.z = pow(result.z, gi);
#endif

	vec4 texColor = vec4(result, materialAlpha);
	if(texColor.a > 0.01){
		color = texColor;
	}else{
		discard;
	}
}