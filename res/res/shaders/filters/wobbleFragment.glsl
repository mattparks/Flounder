#version 130

in vec2 pass_textureCoords;

out vec4 out_colour;

layout(binding = 0) uniform sampler2D originalTexture;

uniform float moveIt;

const float di = 1.0 / 64.0;

void main(void) {
	vec2 t0 = pass_textureCoords;
	t0.x += cos(pass_textureCoords.y * 4.0 * 2.0 * 3.14 + moveIt) * di;
	t0.y += sin(pass_textureCoords.x * 4.0 * 2.0 * 3.14 + moveIt) * di;
	out_colour = texture(originalTexture, t0);
}
