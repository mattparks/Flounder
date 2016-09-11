#version 130

layout(location = 0) out vec4 out_colour;
layout(location = 1) out vec4 out_depth;
layout(location = 2) out vec4 out_normal;
layout(location = 3) out vec4 out_specular;

varying vec2 textureCoords1;
varying vec2 textureCoords2;
varying float textureBlendFactor;
varying float textureTransparency;
varying vec4 particlePosition;

layout(binding = 0) uniform sampler2D particleTexture;

void main(void) {
    vec4 colour1 = texture(particleTexture, textureCoords1);
    vec4 colour2 = texture(particleTexture, textureCoords2);

	out_colour = mix(colour1, colour2, textureBlendFactor);
	out_colour.a -= textureTransparency;

	if (out_colour.a <= 0.0) {
	    out_colour = vec4(0.0);
	    discard;
	}

	out_depth = particlePosition;
	out_normal = vec4(0.0, 1.0, 0.0, 0.0);
	out_specular = vec4(0.0, 0.0, 0.0, 0.0);
}
