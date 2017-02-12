layout (location = 0) in vec3 position; 
layout (location = 1) in vec3 normalVec; 
layout (location = 2) in vec2 texCoord; 
layout (location = 3) in ivec3 boneIDs;
layout (location = 4) in vec3 boneWeights;

out vec2 tex;
out vec3 pos;
out vec3 normal;

out vec4 color;

layout (std140) uniform Matrices{	
	uniform mat4 projection;
	uniform mat4 view;
	uniform mat4 projectionView;
};

uniform mat4 model;


void main(){ 
	pos = vec3(model * vec4(position, 1.0f));
	gl_Position = projectionView * vec4(pos, 1);
	tex = vec2(texCoord.x, texCoord.y);
	normal = mat3(model) * normalVec;

	color = vec4(boneWeights, 1);
}