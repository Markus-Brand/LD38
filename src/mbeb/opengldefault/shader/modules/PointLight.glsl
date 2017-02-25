/**
 * Provides the full logic to calculate the lighting by PointLights
 */

#include modules/Struct_PointLight.glsl

#include modules/Attenuation.glsl

vec3 calcPointLight(PointLight light, vec3 norm, vec3 viewDir, vec3 fragmentPos, float specularStrength){
	vec3 fragToLight = light.position - fragmentPos;

	float lightDistance = length(fragToLight);
	fragToLight = normalize(fragToLight);

	float diff = max(dot(norm, fragToLight), 0.0);
	vec3 diffuse = diff * light.color;

	vec3 halfwayDir = normalize(fragToLight + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0f), SHININESS);
	vec3 specular = specularStrength * spec * light.color;
	
	float attenuation = calculateAttenuation(lightDistance, light.constant, light.linear, light.quadratic);

	diffuse  *= attenuation;
	specular *= attenuation;  

	return vec3(texture(u_texture, tex)) * diffuse + vec3(texture(u_texture, tex)) * specular;
}