#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;
uniform float moveIt;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------CONSTANT------------
const float di = 1.0 / 64.0;

//---------MAIN------------
void main(void) {
	vec2 t0 = pass_textureCoords;
	t0.x += cos(pass_textureCoords.y * 4.0 * 2.0 * 3.14 + moveIt) * di;
	t0.y += sin(pass_textureCoords.x * 4.0 * 2.0 * 3.14 + moveIt) * di;
	out_colour = texture(originalTexture, t0);
}
