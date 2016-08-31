#version 130

in vec2 pass_textureCoords;

out vec4 out_colour;

layout(binding = 0) uniform sampler2D originalTexture;

const float width = 800.0;
const float height = 600.0;

void main(void) {
	vec2 coord;

	if (pass_textureCoords.x < (1.0 - 0.005)) {
		float dx = 8.0 * (1.0 / width);
		float dy = 8.0 * (1.0 / height);
		coord.x = dx * floor(pass_textureCoords.x / dx);
		coord.y = dy * floor(pass_textureCoords.y / dy);
	} else if (pass_textureCoords.x > (1.0 + 0.005)) {
		coord = pass_textureCoords;
	}

    vec3 colour = texture(originalTexture, coord).rgb;
    out_colour = vec4(colour, 1.0);
}
