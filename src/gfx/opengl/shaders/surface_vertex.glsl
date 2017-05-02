#version 130

in vec3 in_position;
in vec3 in_normal;
in vec4 in_color;
in vec2 in_uv;

uniform mat4 in_modelviewprojection;

out vec3 position;
out vec3 normal;
out vec4 color;
out vec2 uv;

void main()
{
    gl_Position = in_modelviewprojection * vec4(in_position, 1.0);

    //position = in_position;
    uv       = in_uv;
    normal   = in_normal;
    color    = in_color;

    return;
}