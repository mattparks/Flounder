#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;
uniform vec3 sunPosition;
uniform float worldHeight;
uniform float aspectRatio;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

vec3 lensflare(vec2 uv, vec3 pos) {
	vec2 uvd = uv * (length(uv));
	vec3 colour = vec3(0.0);

    if (sunPosition.z >= 0.0) {
        vec2 uvx = mix(uv, uvd, 1.0);
        float f2 = max(1.0 / (1.0 + 32.0 * pow(length(uvx + 0.8 * pos.xy), 2.0)), 0.0) * 0.25;
        float f22 = max(1.0 / (1.0 + 32.0 * pow(length(uvx + 0.85 * pos.xy), 2.0)), 0.0) * 0.23;
        float f23 = max(1.0 / (1.0 + 32.0 * pow(length(uvx + 0.9 * pos.xy), 2.0)), 0.0) * 0.21;

        uvx = mix(uv, uvd, -0.5);
        float f4 = max(0.01 - pow(length(uvx + 0.4 * pos.xy), 2.4), 0.0) * 6.0;
        float f42 = max(0.01 - pow(length(uvx + 0.45 * pos.xy), 2.4), 0.0) * 5.0;
        float f43 = max(0.01 - pow(length(uvx + 0.5 * pos.xy), 2.4), 0.0) * 3.0;

        uvx = mix(uv, uvd, -0.4);
        float f5 = max(0.01 - pow(length(uvx + 0.2 * pos.xy), 5.5), 0.0) * 2.0;
        float f52 = max(0.01 - pow(length(uvx + 0.4 * pos.xy), 5.5), 0.0) * 2.0;
        float f53 = max(0.01 - pow(length(uvx + 0.6 * pos.xy), 5.5), 0.0) * 2.0;

        uvx = mix(uv, uvd, -0.5);
        float f6 = max(0.01 - pow(length(uvx - 0.3 * pos.xy), 1.6), 0.0) * 6.0;
        float f62 = max(0.01 - pow(length(uvx - 0.325 * pos.xy), 1.6), 0.0) * 3.0;
        float f63 = max(0.01 - pow(length(uvx - 0.35 * pos.xy), 1.6), 0.0) * 5.0;

        colour.r += f2 + f4 + f5 + f6;
        colour.g += f22 + f42 + f52 + f62;
        colour.b += f23 + f43 + f53 + f63;
	}

    // Hides flare when below a world height.
    colour *= clamp(worldHeight + 2.0, 0.0, 1.0);

    // Adds a bit of darkining around the edge of the screen.
	return colour * 1.3 - vec3(length(uvd) * 0.05);
}

//---------MAIN------------
void main(void) {
	vec3 colour = vec3(1.4, 1.2, 1.0) * lensflare((pass_textureCoords - 0.5) * aspectRatio, sunPosition);
	out_colour = texture(originalTexture, pass_textureCoords) + vec4(colour, 0.0);
}