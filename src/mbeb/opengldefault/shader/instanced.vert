layout (location = 0) in vec3 position; 
layout (location = 1) in vec3 normalVec; 
layout (location = 2) in vec2 texCoord; 
layout (location = 3) in mat4 model;

out vec2 tex;
out vec3 pos;
out vec3 normal;

#include modules/UBO_Matrices.glsl


void main(){ 
	pos = vec3(model * vec4(position, 1.0f));
	gl_Position = projectionView * vec4(pos, 1);
	tex = texCoord.xy;
	normal = mat3(model) * normalVec;
}