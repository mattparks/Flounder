#version 130

layout(location = 0) out vec4 out_colour;
layout(location = 1) out vec4 out_position;
layout(location = 2) out vec4 out_normal;
layout(location = 3) out vec4 out_specular;

varying vec4 positionRelativeToCam;
varying vec3 surfaceNormal;

uniform vec3 colour;

void main(void) {
    out_colour = vec4(colour, 1.0);
    out_position = positionRelativeToCam;
    out_normal = vec4(surfaceNormal, 1.0);
    out_specular = vec4(0.4);
}
