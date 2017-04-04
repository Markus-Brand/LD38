layout (location = 0) in vec3 position; 
layout (location = 1) in vec3 normalVec; 
layout (location = 2) in vec2 texCoord; 
layout (location = 3) in vec3 boneIDs;
layout (location = 4) in vec3 boneWeights;

const int MAX_JOINTS = 50;//max joints allowed in a skeleton//todo remove this
#define MAX_WEIGHTS 3
//max number of joints that can affect a vertex

out vec2 tex;
out vec3 pos;
out vec3 normal;

#include modules/UBO_Matrices.glsl

uniform mat4 boneTransforms[MAX_JOINTS];

uniform mat4 model;


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

	/*/for(int i = 0; i < MAX_WEIGHTS; i++) {
		int id = max(int(boneIDs[i]), 0);
		mat4 boneTransform = boneTransforms[id];
		vec4 posePosition = boneTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * boneWeights[i];
		weightSum += boneWeights[i];
		
		vec4 worldNormal = boneTransform * vec4(normalVec, 0.0);
		totalNormal += worldNormal * boneWeights[i];


	}/**/
	//totalLocalPos /= weightSum;

	

	pos = vec3(model * totalLocalPos);
	gl_Position = projectionView * vec4(pos, 1.0);
	normal = mat3(model) * normalize(totalNormal.xyz);
	tex = texCoord;
}