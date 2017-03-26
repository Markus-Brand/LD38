
in vec2 tex;
in vec3 pos;
flat in vec4 colorInfo;

uniform sampler2D u_texture;
uniform sampler2D u_lut;


void main(){
	vec4 sampledColor = texture(u_texture, tex);
	if(colorInfo.x > 0){
		gl_FragColor = texture(u_lut, vec2(sampledColor.r, colorInfo.y)) * vec4(vec3(1), sampledColor.a);
	}else{
		gl_FragColor = sampledColor;
	}
}