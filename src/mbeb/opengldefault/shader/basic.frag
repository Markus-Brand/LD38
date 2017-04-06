#define SHININESS 256

in vec2 tex;
in vec3 pos;
in vec3 normal;

out vec4 color;

uniform sampler2D u_texture;

#include modules/Struct_DirLight.glsl
#include modules/Struct_PointLight.glsl
#include modules/Struct_SpotLight.glsl

float ambientStrength = 0.1f;
float specularStrength = 2.5f;

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
	vec3 norm = normalize(normal);

    vec4 textureColor = texture(u_texture, tex);
    vec3 materialColor = textureColor.rgb;

	vec3 viewDir = normalize(viewPos - pos);

	vec3 result = vec3(0); 

	for(int i = 0; i < numPointLights; i++){
		result += calcPointLight(pointLights[i], norm, viewDir, materialColor);
	}

	for(int i = 0; i < numDirectionalLights; i++){
		result += calcDirectionalLight(directionalLights[i], norm, viewDir, materialColor);
	}

	for(int i = 0; i < numSpotLights; i++){
		result += calcSpotLight(spotLights[i], norm, viewDir, materialColor);
	}

	vec3 ambient = ambientStrength * materialColor;

	result += ambient;

	//float gamma = 2.2;
	//float gi = 1.0 / gamma;
	//result.x = pow(result.x, gi);
	//result.y = pow(result.y, gi);
	//result.z = pow(result.z, gi);

	if(alpha == 0){
		color = vec4(result, 1.0f);
	}else if(alpha == 1){
		vec4 texColor = vec4(result, textureColor.a);
		if(texColor.a > 0.01){
			color = texColor;
		}else{
			discard;
		}
	}
}