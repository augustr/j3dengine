#version 130

in vec3 in_position;
in vec3 in_normal;
in vec4 color;
in vec2 uv;

out vec4 out_color;

uniform sampler2D texture_0;

void main() {
    out_color = texture2D(texture_0, uv) * color;
}