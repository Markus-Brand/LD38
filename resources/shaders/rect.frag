
in vec2 tex;

uniform sampler2D u_texture;

void main(){
	gl_FragColor = texture(u_texture, tex);
}