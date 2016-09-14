#version 130

//---------IN------------
in vec4 pass_positionRelativeToCam;
in vec3 pass_surfaceNormal;

//---------UNIFORM------------
uniform vec3 colour;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
    out_colour = vec4(colour, 1.0);
}
