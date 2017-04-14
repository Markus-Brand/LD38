//one uniform "material", one sampler2dArray "materialData" and convenience getters

#include modules/Struct_Material.glsl

uniform Material material;

vec4 materialFetch(const in int component, const in vec2 uv) {
    return materialFetch(material, component, uv);
}