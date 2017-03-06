#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;
layout(binding = 1) uniform sampler2D bloomTexture;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------MAIN------------
void main(void) {
	vec4 colour = texture(originalTexture, pass_textureCoords);
	vec4 bloomColour = texture(bloomTexture, pass_textureCoords);
	out_colour = vec4(colour.rgb + bloomColour.rgb, 1.0);

    /*const float gamma = 2.2;
    const float exposure = 0.5;
    colour += bloomColour;
    vec3 result = vec3(1.0) - exp(-colour.rgb * exposure);
    result = pow(result, vec3(1.0 / gamma));
    out_colour = vec4(result, 1.0f);*/
}
