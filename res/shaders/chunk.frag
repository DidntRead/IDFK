#version 400
#extension GL_ARB_bindless_texture : require

in float textureIndex_pass;

out vec4 fragColor;

layout(bindless_sampler) uniform sampler2DArray blocksTexture;

void main() {
	fragColor = vec4(0.0, 0.0, 0.2, 1.0);
}
