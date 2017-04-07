
in vec2 tex;

uniform sampler2D u_texture;

out vec4 color;

void main(){
	color = texture(u_texture, tex);
}