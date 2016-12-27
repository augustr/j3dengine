#version 130

in vec3 in_position;
in vec3 in_normal;
in vec4 in_color;
in vec2 in_uv;

out vec3 position;
out vec3 normal;
out vec4 color;
out vec2 uv;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main()
{
    gl_Position = gl_ModelViewProjectionMatrix * vec4(in_position, 1.0);

    position = in_position;
    uv       = in_uv;
    normal   = in_normal;
    color    = in_color;
    color    = vec4(rand(position.xy), rand(position.xy), rand(position.xy), 1.0);

    return;
}