
in vec2 tex;
in vec3 pos;
in float prog;

uniform sampler2D u_texture;

const float PI = 3.14159265358979;

out vec4 color;

void main(){
	float len = length(pos);
	if(len > 0.45 || len < 0.3){
		float minOffset = min(abs(len - 0.45), abs(len - 0.3));
		if(minOffset < 0.01){			
			gl_FragColor = vec4(0, 0, 0, 1);
			return;	
		}
		discard;
	}
	float angle = atan(pos.x, pos.y);
	float expected = (prog - 0.5) * 2 * PI;
	color = mix(vec4(0, 1, 0, 1), vec4(0.1, 0.1, 0.1, 1), max(0, min(1, 4 * (angle - expected)))) + vec4(max(0, prog - 1), 0, max(0, prog - 1), 0);		
}