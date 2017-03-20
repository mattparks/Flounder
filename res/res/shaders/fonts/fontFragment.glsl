#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D fontTexture;
uniform bool polygonMode;
uniform vec4 colour;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
	out_colour = vec4(colour.rgb, texture(fontTexture, pass_textureCoords).a);

	if (polygonMode) {
		out_colour = vec4(1.0, 0.0, 0.0, 1.0);
	}

	if (out_colour.a < 0.05){
		out_colour = vec4(0.0);
		discard;
	}
}
