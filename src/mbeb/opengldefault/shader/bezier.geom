#define SAMPLES 100
layout (lines) in;
layout (line_strip, max_vertices = SAMPLES) out;

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

vec4 progressVector(const in float progress){
	return vec4(pow(progress, 3), pow(progress, 2), progress, 1);
}

void main(){ 
	float stepSize = 1.0f / float(SAMPLES - 1)
	for(int i = 0; i < SAMPLES; i++){
		pos = vec3(bezier * progressVector(mix(gs_in[0].progress, gs_in[1].progress, float(i) * stepSize)));
		gl_Position = projectionView * vec4(pos, 1);
   		EmitVertex();
	}
    EndPrimitive();
}