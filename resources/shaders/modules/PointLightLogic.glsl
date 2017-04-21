vec3 calcPointLight(
	const in PointLight light,
	const in vec3 norm,
	const in vec3 viewPos,
	const in vec3 viewDir,
	const in vec3 diffuseColor,
	const in vec3 specularColor,
	const in int shininess
) {
	vec3 direction = light.position - viewPos;

	float distance = length(direction);
	direction = normalize(direction);

	float diff = max(dot(norm, direction), 0.0);
	vec3 diffuse = diff * light.color;

	vec3 halfwayDir = normalize(direction + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0), shininess);
	vec3 specular = specularStrength * spec * light.color;

	float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * distance * distance);

	diffuse  *= attenuation;
	specular *= attenuation;

	return diffuseColor * diffuse + specularColor * specular;
}
