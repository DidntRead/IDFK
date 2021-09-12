#version 400
#extension GL_ARB_bindless_texture : require

in vec2 texture_cord;

out vec4 fragColor;

layout(bindless_sampler) uniform sampler2D crosshair;

void main() {
	fragColor = texture(crosshair, texture_cord);
}