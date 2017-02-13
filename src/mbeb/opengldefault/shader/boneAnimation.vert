layout (location = 0) in vec3 position; 
layout (location = 1) in vec3 normalVec; 
layout (location = 2) in vec2 texCoord; 
layout (location = 3) in ivec3 boneIDs;
layout (location = 4) in vec3 boneWeights;

const int MAX_JOINTS = 50;//max joints allowed in a skeleton//todo remove this
#define MAX_WEIGHTS 3
//max number of joints that can affect a vertex

out vec2 tex;
out vec3 pos;
out vec3 normal;

out vec4 color;

layout (std140) uniform Matrices{	
	uniform mat4 projection;
	uniform mat4 view;
	uniform mat4 projectionView;
};

uniform mat4 boneTransforms[MAX_JOINTS];

uniform mat4 model;


void main() {
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	
	/**/for(int i = 3; i < 4; i++) {
		mat4 boneTransform = boneTransforms[boneIDs[i]];
		vec4 posePosition = boneTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * boneWeights[i];
		
		vec4 worldNormal = boneTransform * vec4(normalVec, 0.0);
		totalNormal += worldNormal * boneWeights[i];


	}  /*/


		mat4 boneTransform1 = boneTransforms[boneIDs.x];
		vec4 posePosition1 = boneTransform1 * vec4(position, 1.0);
		totalLocalPos += posePosition1 * boneWeights.x;
		
		vec4 worldNormal1 = boneTransform1 * vec4(normalVec, 0.0);
		totalNormal += worldNormal1 * boneWeights.x;

		mat4 boneTransform2 = boneTransforms[boneIDs.y];
		vec4 posePosition2 = boneTransform2 * vec4(position, 1.0);
		totalLocalPos += posePosition2 * boneWeights.y;
		
		vec4 worldNormal2 = boneTransform2 * vec4(normalVec, 0.0);
		totalNormal += worldNormal2 * boneWeights.y;

		mat4 boneTransform3 = boneTransforms[boneIDs.z];
		vec4 posePosition3 = boneTransform3 * vec4(position, 1.0);
		totalLocalPos += posePosition3 * boneWeights.z;
		
		vec4 worldNormal3 = boneTransform3 * vec4(normalVec, 0.0);
		totalNormal += worldNormal3 * boneWeights.z;/**/
	
	totalLocalPos.w = 1;
	totalNormal.w = 1;

	pos = vec3(model * totalLocalPos);
	gl_Position = projectionView * vec4(pos, 1.0);
	normal = mat3(model) * totalNormal.xyz;
	tex = texCoord;

	color = vec4(boneWeights, 1);
}