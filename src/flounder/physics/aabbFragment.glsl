#version 130

out vec4 out_colour;

varying vec4 pass_positionRelativeToCam;
varying vec3 pass_colour;

void main(void) {
    out_colour = vec4(pass_colour, 1.0);
}
