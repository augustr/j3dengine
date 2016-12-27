#version 130

in vec3 position;
in vec3 normal;
in vec4 color;
in vec2 uv;

out vec4 out_color;

uniform sampler2D material0;
uniform sampler2D material1;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    float light = 0;
    vec3 lightDir = normalize(vec3(-0.1, 0.5, 1.0f));

    // Pre-calculated light
    light = color.r;
    //light = 1.0;

    // Real-time light
    light = (pow(dot(normal,lightDir),2.5)+0.3);

    // Apply grass texture with light
    out_color.rgb = texture2D(material0, uv).rgb*light;

    // Alpha blend in rock (slope is in alpha channel)
    float a = color.a;
    //a = rand(position.xy);
    out_color.rgb = out_color.rgb*(1.0-a) + (a)*texture2D(material1, uv).rgb*light;

    out_color = color;
}