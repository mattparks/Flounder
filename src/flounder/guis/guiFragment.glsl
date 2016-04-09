#version 130

out vec4 out_colour;

varying vec2 pass_textureCoords;

layout(binding = 0) uniform sampler2D guiTexture;

uniform float alpha;
uniform vec3 colourOffset;

void main(void) {
	out_colour = texture(guiTexture, pass_textureCoords) + vec4(colourOffset, 0.0);
	out_colour.a *= alpha;
}
