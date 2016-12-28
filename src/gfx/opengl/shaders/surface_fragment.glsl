#version 130

in vec3 position;
in vec3 normal;
in vec4 color;
in vec2 uv;

out vec4 out_color;

uniform sampler2D material0;
uniform sampler2D material1;

void main() {
    float light = 0;
    vec3 lightDir = normalize(vec3(0.1, 0.5, 0.7f));

    // Pre-calculated light
    //light = color.r;

    // Real-time light
    light = clamp(dot(normal,lightDir), 0, 1);

    // Apply grass texture with light
    out_color.rgb = texture2D(material0, uv).rgb*light;

    // Alpha blend in rock (slope is in alpha channel)
    float a = color.a;
    out_color.rgb = out_color.rgb*(1.0-a) + (a)*texture2D(material1, uv).rgb*light;
}