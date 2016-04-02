#version 130

out vec4 out_colour;

varying vec2 pass_textureCoords;

layout(binding = 0) uniform sampler2D guiTexture;

uniform float alpha;

void main(void) {
	out_colour = texture(guiTexture, pass_textureCoords);
	out_colour.a *= alpha;
}
