#version 400
#extension GL_ARB_bindless_texture : require

in vec3 position_pass;
in float textureIndex_pass;

out vec4 fragColor;

layout(bindless_sampler) uniform sampler2DArray blocksTexture;

void main() {
    vec3 normal = normalize(cross(dFdy(position_pass), dFdx(position_pass)));

    vec2 texturePos;

    if (all(greaterThan(vec2(normal.z), normal.xy)) || all(lessThan(vec2(normal.z), normal.xy))) {
        texturePos = vec2(1) - position_pass.xy;
    } else if (all(greaterThan(vec2(normal.y), normal.xz)) || all(lessThan(vec2(normal.y), normal.xz))) {
        texturePos = position_pass.xz;
    } else {
        texturePos = vec2(1) - position_pass.zy;
    }

	fragColor = texture(blocksTexture, vec3(texturePos, round(textureIndex_pass)));
}