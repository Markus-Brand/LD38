out vec4 color;

in vec3 pos;

uniform vec3 boxColor;

void main() {
    color = vec4(boxColor, 1);
}  