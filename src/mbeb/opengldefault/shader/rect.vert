layout (location = 0) in vec2 position; 

#include modules/UBO_Matrices.glsl

out vec2 tex;

void main(){
	tex = (position + vec2(1, 1)) / 2;
	gl_Position = projectionView * vec4(position, 2, 1);
}