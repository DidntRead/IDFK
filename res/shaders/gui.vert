#version 400

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 texture;

out vec2 texture_cord;

void main() {
    texture_cord = texture;
	gl_Position = vec4(position, 0, 1.0);
}
