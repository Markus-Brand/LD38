vec3 calcPointLight(PointLight light, vec3 norm, vec3 viewDir, vec3 materialColor){

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

	return materialColor * diffuse + specular;
}
