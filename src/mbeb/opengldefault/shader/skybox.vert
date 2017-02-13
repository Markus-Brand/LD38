layout (location = 0) in vec3 position;

out vec3 tex;

layout (std140) uniform Matrices{	
	uniform mat4 projection;
	uniform mat4 view;
	uniform mat4 projectionView;
	uniform mat4 skyboxView;
};

void main(){
	vec4 pos = projection * skyboxView * vec4(position, 1.0f);
	gl_Position = pos.xyww;
	tex = position;
}