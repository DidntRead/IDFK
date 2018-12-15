#version 400
#extension GL_ARB_bindless_texture : require

in vec3 position_pass;
in float textureIndex_pass;

out vec4 fragColor;

layout(bindless_sampler) uniform sampler2DArray blocksTexture;

void main() {
    vec3 normalAbs = abs(normalize(cross(dFdy(position_pass), dFdx(position_pass))));

    vec2 texturePos = vec2(1);

    if (all(greaterThan(normalAbs.zz, normalAbs.xy))) {
        texturePos -= position_pass.xy;
    } else if (all(greaterThan(normalAbs.yy, normalAbs.xz))) {
        texturePos -= position_pass.xz;
    } else {
        texturePos -= position_pass.zy;
    }

	fragColor = texture(blocksTexture, vec3(texturePos, round(textureIndex_pass)));
}