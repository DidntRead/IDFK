#version 400 core
#extension GL_ARB_bindless_texture : require

precision mediump float;

in vec2 Frag_UV;
in vec4 Frag_Color;

out vec4 Out_Color;

layout(bindless_sampler) uniform sampler2D Texture;

void main(){
    Out_Color = Frag_Color * texture(Texture, Frag_UV.st);
}