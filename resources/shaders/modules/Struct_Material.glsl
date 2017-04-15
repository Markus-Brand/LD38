struct Material {
    sampler2DArray textureLayers;
    int shininess;
};

vec4 materialFetch(
    const in Material material,
    const in int component,
    const in vec2 uv
    ) {
    return texture(material.textureLayers, vec3(uv, component));
}

int materialShininess(const in Material material) {
    return material.shininess;
}