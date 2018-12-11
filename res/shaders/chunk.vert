#version 400

layout(location = 0) in vec3 position;
layout(location = 1) in float textureIndex;

out float textureIndex_pass;

uniform mat4 projView;

void main() {
    textureIndex_pass = textureIndex;
	gl_Position = projView * vec4(position, 1.0);
}
