//this little fragment calculates the tbn-matrix (tangent, bitangent, normal) that is needed for normal mapping
//it needs "tangent" and "normal" as input (vec3 each), which represent the global space tangent and normal
//it stores the resulting matrix in a (previously declared) variable "tbn" of type mat3


//gram-schmidt-correction to re-orthogonalize
tangent = normalize(tangent - dot(tangent, normal) * normal);
vec3 bitangent = cross(tangent, normal);
tbn = mat3(tangent, bitangent, normal);