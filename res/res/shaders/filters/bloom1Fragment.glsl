#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------CONSTANT------------
const float BLOOM_BRIGHT = 0.652;

//---------MAIN------------
void main(void) {
	vec3 colour = texture(originalTexture, pass_textureCoords).rgb;
	float brightness = length(colour.rgb);
	out_colour = vec4(0.0);

	if (brightness > BLOOM_BRIGHT){
		brightness -= BLOOM_BRIGHT;
		out_colour.rgb = colour.rgb * clamp(brightness * 2.0, 0.0, 1.0);
	}
}
