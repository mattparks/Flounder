#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;
uniform float bloomThreshold;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
	vec3 colour = texture(originalTexture, pass_textureCoords).rgb;
//	float brightness = dot(colour.rgb, vec3(0.2126, 0.7152, 0.0722));
	float brightness = length(colour.rgb);
	out_colour = vec4(0.0);

	if (brightness > bloomThreshold){
		brightness -= bloomThreshold;
		out_colour.rgb = colour.rgb * clamp(brightness * 2.0, 0.0, 1.0);
	}
}
