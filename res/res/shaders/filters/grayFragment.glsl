#version 130

in vec2 pass_textureCoords;

layout(location = 0) out vec4 out_colour;

layout(binding = 0) uniform sampler2D originalTexture;

void main(void) {
    vec3 colour = texture(originalTexture, pass_textureCoords).rgb;
	float gray = dot(colour, vec3(0.299, 0.587, 0.114));
	out_colour = vec4(gray, gray, gray, 1.0);
}
