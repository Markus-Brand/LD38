layout (location = 0) in vec3 position; 
layout (location = 1) in vec3 normalVec;
layout (location = 2) in vec3 tangentVec;
layout (location = 3) in vec2 texCoord;

out vec2 tex;
out vec3 pos;
out vec3 normal;
out mat3 tbn;

#include modules/UBO_Matrices.glsl

uniform mat4 model;

void main(){ 
	pos = vec3(model * vec4(position, 1.0f));
	gl_Position = projectionView * vec4(pos, 1);
	tex = vec2(texCoord.x, texCoord.y);
	normal = normalize(mat3(model) * normalVec);
	vec3 tangent = mat3(model) * tangentVec;

    #include modules/tangentSpace.glsl
}