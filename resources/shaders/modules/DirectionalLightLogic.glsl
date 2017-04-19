vec3 calcDirectionalLight(
	const in DirectionalLight light,
	const in vec3 norm,
	const in vec3 viewDir,
	const in vec3 diffuseColor,
	const in vec3 specularColor,
	const in int shininess
) {
	vec3 direction = normalize(-light.direction);

	float diff = max(dot(norm, direction), 0.0);
	vec3 diffuse = diff * light.color;

	vec3 halfwayDir = normalize(direction + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0f), shininess);
	vec3 specular = specularStrength * spec * light.color;

	return diffuseColor * diffuse + specularColor * specular;
}