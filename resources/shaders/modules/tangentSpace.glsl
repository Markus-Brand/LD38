//this little fragment calculates the tbn-matrix (tangent, bitangent, normal) that is needed for normal mapping

mat3 tangentSpace(const in vec3 tangent, const in vec3 normal) {
    //gram-schmidt-correction to re-orthogonalize
    vec3 tangentBetter = normalize(tangent - dot(tangent, normal) * normal);
    vec3 bitangent = cross(tangentBetter, normal);
    return mat3(tangentBetter, bitangent, normal);
}