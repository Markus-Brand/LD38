layout (location = 0) in vec3 position; 

out vec3 pos;

layout (std140) uniform Matrices{	
	uniform mat4 projection;
	uniform mat4 view;
	uniform mat4 projectionView;
	uniform mat4 skyboxView;
};

uniform mat4 model;


void main(){ 
	pos = vec3(model * vec4(position, 1.0f));
	gl_Position = projectionView * vec4(pos, 1);
}