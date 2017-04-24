layout (location = 0) in vec3 position; 
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec4 colorInformation; 
layout (location = 3) in mat4 model; 
layout (location = 7) in vec4 offset;

layout (location = 8) in vec4 in_color1;
layout (location = 9) in vec4 in_color2;
layout (location = 10) in vec4 in_color3;

out vec2 tex;
out vec3 pos;

flat out vec4 colorInfo;

flat out vec3 color1;
flat out float progress1;
flat out vec3 color2;
flat out float progress2;
flat out vec3 color3;


void main(){ 
	colorInfo = colorInformation;
	pos = vec3(model * vec4(position, 1.0f));
	gl_Position = vec4(pos, 1);
	tex = texCoord.xy;// / offset.xy + offset.zw; //breaks things

	color1 = in_color1.rgb;
	progress1 = in_color1.a;
	color2 = in_color2.rgb;
	progress2 = in_color2.a;
	color3 = in_color3.rgb;
}