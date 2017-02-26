in vec3 tex;
out vec4 color;

uniform samplerCube u_cubeMap;

void main(){
	color = texture(u_cubeMap, tex);
}