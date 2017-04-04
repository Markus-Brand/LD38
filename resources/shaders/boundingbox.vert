layout (location = 0) in vec3 position; 

out vec3 pos;

#include modules/UBO_Matrices.glsl

uniform mat4 model;

void main(){ 
	pos = vec3(model * vec4(position, 1.0f));
	gl_Position = projectionView * vec4(pos, 1);
}