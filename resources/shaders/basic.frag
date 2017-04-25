
in vec2 frag_in_tex;
in vec3 frag_in_pos;
in vec3 frag_in_norm;
in mat3 frag_in_tbn; //tangent-bitangent-normal (needed for normal mapping)

out vec4 color;

#include modules/MaterialUniform.glsl

#include modules/Struct_DirLight.glsl
#include modules/Struct_PointLight.glsl
#include modules/Struct_SpotLight.glsl

float ambientStrength = 0.03;
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
	vec3 normal = frag_in_norm;

	vec2 yflip = vec2(frag_in_tex.x, 1.0 - frag_in_tex.y);

	vec4 diffuseColorAlpha = materialDiffuseAlpha(frag_in_tex);
	vec3 diffuseColor = diffuseColorAlpha.rgb;
	float materialAlpha = diffuseColorAlpha.a;
	vec3 specularColor = materialSpecular(frag_in_tex);
	vec3 emissionColor = materialEmit(frag_in_tex);
	vec3 normalFromMap = materialNormal(yflip);
	int shininess = materialShininess();

	vec3 viewDir = normalize(viewPos - frag_in_pos);

	//normal mapping
	if (length(normalFromMap) > 0.01) {
		normalFromMap = normalize(normalFromMap * 2.0 - 1.0);
		normal = normalize(frag_in_tbn * normalFromMap);
	}

	//normal flipping
	if (dot(viewDir,normal) < 0) {
	    normal = -normal;
	}


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

	//emission
	result += emissionColor;


//#ifdef GAMMA_CORRECTION
	float gamma = 1.1;
	float gammaInverse = 1.0 / gamma;
	result.x = pow(result.x, gammaInverse);
	result.y = pow(result.y, gammaInverse);
	result.z = pow(result.z, gammaInverse);
//#endif

	vec4 texColor = vec4(result, materialAlpha);
	if(texColor.a > 0.5){
		color = texColor;
	}else{
		discard;
	}
}