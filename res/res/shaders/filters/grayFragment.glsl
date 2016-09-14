#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
    vec3 colour = texture(originalTexture, pass_textureCoords).rgb;
	float gray = dot(colour, vec3(0.299, 0.587, 0.114));
	out_colour = vec4(gray, gray, gray, 1.0);
}
