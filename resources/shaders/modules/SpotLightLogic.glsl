vec3 calcSpotLight(
	const in SpotLight light,
	const in vec3 norm,
	const in vec3 viewDir,
	const in vec3 materialColor,
	const in vec3 specularColor,
	const in int shininess
) {
	vec3 direction = light.position - pos;

	float distance = length(direction);
	direction = normalize(direction);

	float diff = max(dot(norm, direction), 0.0);
	vec3 diffuse = diff * light.color;

	vec3 halfwayDir = normalize(direction + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0f), shininess);
	vec3 specular = specularStrength * spec * light.color;

	float attenuation = 1.0f / (light.constant + light.linear * distance + light.quadratic * distance * distance);

	float intensity = clamp((dot(direction, normalize(-light.direction)) - light.outerCutoff) / (light.cutoff - light.outerCutoff), 0.0f, 1.0f);

	diffuse *= intensity;
	specular *= intensity * 2;

	diffuse  *= attenuation;
	specular *= attenuation;

	return materialColor * diffuse + specularColor * specular;
}
