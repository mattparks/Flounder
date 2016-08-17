#version 130

layout(location = 0) out vec4 out_colour;

varying vec4 positionRelativeToCam;
varying vec3 surfaceNormal;

uniform vec3 colour;

void main(void) {
    out_colour = vec4(colour, 1.0);
}
