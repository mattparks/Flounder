#version 130

in vec2 pass_textureCoords;

layout(location = 0) out vec4 out_colour;

layout(binding = 0) uniform sampler2D originalTexture;

void main(void) {
    vec3 colour = texture(originalTexture, pass_textureCoords).rgb;
	out_colour = vec4((1.0 - colour), 1.0);
}
