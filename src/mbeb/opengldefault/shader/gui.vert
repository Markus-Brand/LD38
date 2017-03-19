layout (location = 0) in vec3 position; 
layout (location = 1) in vec2 texCoord; 
layout (location = 3) in mat4 model; 
layout (location = 7) in vec4 offset; 

out vec2 tex;
out vec3 pos;

void main(){ 
	pos = vec3(model * vec4(position, 1.0f));
	gl_Position = vec4(pos, 1);
	tex = texCoord.xy / offset.xy + offset.zw;
}