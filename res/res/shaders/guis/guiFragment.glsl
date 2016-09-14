#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D guiTexture;
uniform float alpha;
uniform vec3 colourOffset;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
	out_colour = texture(guiTexture, pass_textureCoords) + vec4(colourOffset, 0.0);
	out_colour.a *= alpha;
}
