#version 130

in vec4 pass_positionRelativeToCam;
in vec3 pass_surfaceNormal;

layout(location = 0) out vec4 out_colour;

uniform vec3 colour;

void main(void) {
    out_colour = vec4(colour, 1.0);
}
