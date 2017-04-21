layout (location = 0) in vec3 position;

out vec3 tex;

#include modules/UBO_Matrices.glsl

uniform mat4 skyboxView;

void main(){
	vec4 pos = projection * skyboxView * vec4(position, 1.0f);
	//A skybox has no depth
	gl_Position = pos.xyww;
	tex = position;
}