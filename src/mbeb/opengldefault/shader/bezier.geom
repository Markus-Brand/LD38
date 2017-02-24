
#define MAXV 64
#define SAMPLES (MAXV - 4)
layout (lines) in;
layout (line_strip, max_vertices = MAXV) out;

in VS_OUT {
    float progress;
} gs_in[];

layout (std140) uniform Matrices{	
	uniform mat4 projection;
	uniform mat4 view;
	uniform mat4 projectionView;
	uniform mat4 skyboxView;
};

uniform mat4 bezier;
uniform mat4 bernstein;
uniform mat4 model;

out vec4 in_color;

vec4 progressVector(const in float progress){
	return vec4(pow(progress, 3), pow(progress, 2), progress, 1);
}

void main(){ 
	float stepSize = 1.0f / float(SAMPLES - 1);
	for(int i = 0; i < SAMPLES; i++){
		float progress = mix(gs_in[0].progress, gs_in[1].progress, float(i) * stepSize);
		in_color = vec4(progress, 1.0f - progress, 0, 1);
		vec3 pos = vec3(bezier * bernstein * progressVector(progress));
		gl_Position = projectionView * model * vec4(pos, 1);
   		EmitVertex();
	}
    EndPrimitive();
    
	in_color = vec4(0.1, 0.1, 0.1, 1);
	gl_Position = projectionView * model * bezier[0];
	EmitVertex();
	gl_Position = projectionView * model * bezier[1];
	EmitVertex();
	gl_Position = projectionView * model * bezier[2];
	EmitVertex();
	gl_Position = projectionView * model * bezier[3];
	EmitVertex();
	EndPrimitive();
}