#version 130

out vec4 out_colour;

varying vec2 textureCoords1;
varying vec2 textureCoords2;
varying float textureBlendFactor;
varying float textureTransparency;

layout(binding = 0) uniform sampler2D particleTexture;

void main(void) {
    vec4 colour1 = texture(particleTexture, textureCoords1);
    vec4 colour2 = texture(particleTexture, textureCoords2);
	out_colour = mix(colour1, colour2, textureBlendFactor);
	out_colour.a -= textureTransparency;

	// if (out_colour.a < 0.4) {
	//     out_colour = vec4(0.0);
	// 	discard;
	// }
}
