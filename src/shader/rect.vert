
layout (location = 0) in vec2 position; 


layout (std140) uniform Matrices{	
	uniform mat4 projection;
	uniform mat4 view;
	uniform mat4 projectionView;
};

out vec2 tex;

void main(){
	tex = (position + vec2(1, 1)) / 2;
	gl_Position = projectionView * vec4(position, 2, 1);
}