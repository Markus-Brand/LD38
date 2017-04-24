
in vec2 tex;
in vec3 pos;

flat in vec4 colorInfo;

uniform sampler2D u_texture;


//2 borders for nice effects

const vec3 borderColor = vec3(0.7);

flat in vec3 color1;
flat in float progress1;
flat in vec3 color2;
flat in float progress2;
flat in vec3 color3;

out vec4 color;

void main(){

	vec4 sampledColor = texture(u_texture, tex);
	float currentProgressLevel = tex.x;
	float borderLevel = sampledColor.g;

	vec3 outputColor;
	if (currentProgressLevel < progress1) {
	    outputColor = color1;
	} else if (currentProgressLevel < progress2) {
	    outputColor = color2;
	} else {
	    outputColor = color3;
	}

	outputColor = mix(outputColor, borderColor, borderLevel);

	color = vec4(outputColor, sampledColor.a);
}