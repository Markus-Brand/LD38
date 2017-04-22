layout (location = 0) in vec3 position; 
layout (location = 1) in vec3 normalVec;
layout (location = 2) in vec3 tangentVec;
layout (location = 3) in vec2 texCoord;

out vec2 frag_in_tex;
out vec3 frag_in_pos;
out vec3 frag_in_world_pos;
out vec3 frag_in_norm;
out mat3 frag_in_tbn; //tangent-bitangent-normal (needed for normal mapping)

#include modules/UBO_Matrices.glsl

uniform mat4 model;

#include modules/tangentSpace.glsl

void main(){ 
	frag_in_pos = position;
	frag_in_world_pos = vec3(model * vec4(position, 1.0f));
	gl_Position = projectionView * vec4(frag_in_world_pos, 1.0f);
	frag_in_tex = vec2(texCoord.x, texCoord.y);
	frag_in_norm = normalize(mat3(model) * normalVec);
	vec3 tangent = mat3(model) * tangentVec;

    frag_in_tbn = tangentSpace(tangent, frag_in_norm);
}