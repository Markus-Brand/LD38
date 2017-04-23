layout (location = 0) in vec3 position; 

#include modules/UBO_Matrices.glsl

uniform mat4 model;

void main(){ 
	gl_Position = projectionView * model * vec4(position, 1);
}