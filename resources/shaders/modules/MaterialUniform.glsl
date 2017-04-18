//one uniform "material" and convenience getters

#include modules/Struct_Material.glsl

uniform Material material;

vec4 materialFetch(const in int component, const in vec2 uv) {
	return materialFetch(material, component, uv);
}

vec4 materialDiffuseAlpha(const in vec2 uv) {
	return materialFetch(0, uv);
}
vec3 materialSpecular(const in vec2 uv) {
	return materialFetch(1, uv).rgb;
}
vec3 materialEmit(const in vec2 uv) {
	return materialFetch(2, uv).rgb;
}
vec3 materialNormal(const in vec2 uv) {
	return materialFetch(3, uv).rgb;
}
int materialShininess() {
	return materialShininess(material);
}