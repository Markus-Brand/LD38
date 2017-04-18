//one uniform "material" and convenience getters

#include modules/Struct_Material.glsl

uniform Material material;

vec4 materialFetch(const in int component, const in vec2 uv) {
	return materialFetch(material, component, uv);
}

vec3 materialDiffuse(const in vec2 uv) {
	return materialFetch(0, uv).rgb;
}
float materialTransparency(const in vec2 uv) {
	return materialFetch(0, uv).a;
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