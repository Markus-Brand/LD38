layout (location = 0) in vec3 position; 
layout (location = 1) in vec2 texCoord; 
layout (location = 3) in mat4 model; 
layout (location = 7) in vec4 progress; 

out vec2 tex;
out vec3 pos;
out float prog;

void main(){ 
	pos = position - vec3(0.5, 0.5, 0);
	gl_Position = model * vec4(pos, 1);
	tex = texCoord.xy;
	prog = progress.x;
}