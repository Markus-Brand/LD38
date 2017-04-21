layout (location = 0) in vec3 position; 
layout (location = 1) in vec3 normalVec;
layout (location = 2) in vec3 tangentVec;
layout (location = 3) in vec2 texCoord;
layout (location = 4) in vec3 boneIDs;
layout (location = 5) in vec3 boneWeights;

#define MAX_JOINTS 50
//max joints allowed in a skeleton //todo make this dynamic
//#define MAX_WEIGHTS 3
//max number of joints that can affect a vertex, currently unused

out vec2 frag_in_tex;
out vec3 frag_in_pos;
out vec3 frag_in_norm;
out mat3 frag_in_tbn; //tangent-bitangent-normal (needed for normal mapping)

#include modules/UBO_Matrices.glsl

uniform mat4 boneTransforms[MAX_JOINTS];

uniform mat4 model;

#include modules/tangentSpace.glsl

void main() {
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	float weightSum = 0.f;

    mat4 BoneTransform = boneTransforms[max(int(boneIDs[0]), 0)] * boneWeights[0];
    BoneTransform += boneTransforms[max(int(boneIDs[1]), 0)] * boneWeights[1];
    BoneTransform += boneTransforms[max(int(boneIDs[2]), 0)] * boneWeights[2];

	totalLocalPos = BoneTransform * vec4(position, 1.0);
	totalLocalPos.w = 1;
	totalNormal = BoneTransform * vec4(normalVec, 0.0);

	frag_in_pos = vec3(model * totalLocalPos);
	gl_Position = projectionView * vec4(frag_in_pos, 1.0);
	frag_in_norm = normalize(mat3(model) * normalize(totalNormal.xyz));
	vec3 tangent = mat3(model) * tangentVec;
	frag_in_tex = texCoord;

    frag_in_tbn = tangentSpace(tangent, frag_in_norm);
}