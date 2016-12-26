#version 130

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------CONSTANT------------
const float BLUR_AMOUNT = 1.0;
const float CENTRE = 1.1;
const float STEP_SIZE = 0.004;
const float STEPS = 3.0;

const float OFFSET_MIN = (float(STEPS - 1.0)) / -2.0;
const float OFFSET_MAX = (float(STEPS - 1.0)) / +2.0;

//---------MAIN------------
void main(void) {
	// Work out how much to blur based on the mid point.
	float amount = pow((pass_textureCoords.y * CENTRE) * 2.0 - 1.0, 2.0) * BLUR_AMOUNT;
		
	// This is the accumulation of color from the surrounding pixels in the texture.
	out_colour = vec4(0.0, 0.0, 0.0, 1.0);
		
	// From minimum offset to maximum offset.
	for (float offsX = OFFSET_MIN; offsX <= OFFSET_MAX; ++offsX) {
		for (float offsY = OFFSET_MIN; offsY <= OFFSET_MAX; ++offsY) {
			// Copy the coord so we can mess with it.
			vec2 temp_tcoord = pass_textureCoords.xy;

			// Work out which uv we want to sample now.
			temp_tcoord.x += offsX * amount * STEP_SIZE;
			temp_tcoord.y += offsY * amount * STEP_SIZE;

			// Accumulate the sample
			out_colour += texture(originalTexture, temp_tcoord);
		}
	}
		
	// Because we are doing an average, we divide by the amount (x AND y, hence STEPS * steps).
	out_colour /= float(STEPS * STEPS);
}
