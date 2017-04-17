struct Material {
    sampler2DArray textureLayers;
    int shininess;
};

vec4 materialFetch(
    const in Material material,
    const in int component,
    const in vec2 uv
    ) {
    //manual clampToBlack if the material has fewer layers than requested
    ivec3 size = textureSize(material.textureLayers, 0);
    vec4 texColor = texture(material.textureLayers, vec3(uv, component));
    return mix(vec4(0), texColor, step(component + 1, size.z));
}

int materialShininess(const in Material material) {
    return material.shininess;
}