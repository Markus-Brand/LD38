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
