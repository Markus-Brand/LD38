
in vec2 tex;
in vec3 pos;

uniform sampler2D u_texture;

void main(){
	vec4 sampledColor = texture(u_texture, tex);
	if(sampledColor.a < 0.5) discard;
	gl_FragColor = sampledColor;
}