// Based on: https://www.shadertoy.com/view/Xlc3D2#
#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;
uniform vec2 sunPositon;
uniform float aspectRatio;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

float rnd(vec2 p) {
    float f = fract(sin(dot(p, vec2(12.1234, 72.8392)) * 45123.2));
    return f;
}

float rnd(float w) {
    float f = fract(sin(w) * 1000.0);
    return f;
}

float regShape(vec2 p, int N) {
    float f;
    float a = atan(p.x, p.y) + 0.2;
    float b= 6.28319 / float(N);
    f = smoothlyStep(0.5, 0.51, cos(floor(0.5 + a / b) * b - a) * length(p.xy));
    return f;
}

vec3 circle(vec2 p, float size, float decay, vec3 colour, vec3 colour2, float dist, vec2 mouse) {
    // l is used for making rings. The length is got and passed through a sinwave but also it uses a pow function.
    // Pow function + sin function , from 0 and up, = a pulse, at least if return the max of that and 0.0.
    float l = length(p + mouse * (dist * 4.0)) + size / 2.0;

    // l2 is used in the rings as well.
    float l2 = length(p + mouse * (dist * 4.0)) + size / 3.0;

    // These are circles, big, rings, and tiny respectively
    float c = max(0.01 - pow(length(p + mouse * dist), size * 1.4), 0.0) * 50.0;
    float c1 = max(0.001 - pow(l - 0.3, 1.0 / 40.0) + sin(l * 30.0), 0.0) * 3.0;
    float c2 = max(0.04 / pow(length(p - mouse * dist / 2.0 + 0.09) * 1.0, 1.0), 0.0) / 20.0;
    float s = max(0.01 - pow(regShape(p * 5.0 + mouse * dist * 5.0 + 0.9, 6), 1.0), 0.0) * 5.0;

   	colour = 0.5 + 0.5 * sin(colour);
    colour = cos(vec3(0.44, 0.24, 0.2) * 8.0 + dist * 4.0) * 0.5 + 0.5;
 	vec3 f = c * colour;
    f += c1 * colour;

    f += c2 * colour;
    f += s * colour;
    return f - 0.01;
}

float sun(vec2 p, vec2 mouse) {
    float f;
    vec2 sunp = p + mouse;
    float sun = 1.0 - length(sunp) * 8.0;
    return f;
}

//---------MAIN------------
void main(void) {
	vec2 uv = pass_textureCoords;
	uv.x = uv.x * aspectRatio;
    vec2 mm = sunPositon;

    vec3 circleColour = vec3(0.9, 0.2, 0.1);
    vec3 circleColour2 = vec3(0.3, 0.1, 0.9);

	vec3 colour = vec3(0.0, 0.0, 0.0);

	// This calls the function which adds three circle types every time through the loop based on parameters. rnd i * 2000. And rnd i * 20 are just to help randomize things more.
    for(float i = 0.0; i < 10.0; i++){
        colour += circle(uv, pow(rnd(i * 2000.0) * 1.8, 2.0) + 1.41, 0.0, circleColour + i, circleColour2 + i, rnd(i * 20.0) * 3.0 + 0.2 - 0.5, mm);
    }

    // Get angle and length of the sun (uv - sun).
    float a = atan(uv.y - mm.y, uv.x - mm.x);
    float l = max(1.0 - length(uv - mm) - 0.84, 0.0);

    float bright = 0.1;// Add brightness based on how the sun moves so that it is brightest when it is lined up with the center.

    // Add the sun with the frill things.
    colour += max(0.1 / pow(length(uv - mm) * 5.0, 5.0), 0.0) * abs(sin(a * 5.0 + cos(a * 9.0))) / 20.0;
    colour += max(0.1 / pow(length(uv - mm) * 10.0, 1.0 / 20.0), 0.0) + abs(sin(a * 3.0 + cos(a * 9.0))) / 8.0 * (abs(sin(a * 9.0))) / 1.0;

    //A dd another sun in the middle (to make it brighter)  with the20color I want, and bright as the numerator.
    colour += (max(bright/pow(length(uv - mm) * 4.0, 1.0 / 2.0), 0.0) * 4.0) * vec3(0.2, 0.21, 0.3) * 4.0;

    //Multiply by the exponetial e^x ? of 1.0-length which kind of masks the brightness more so that there is a sharper roll of of the light decay from the sun.
    colour *= exp(1.0 - length(uv - mm)) / 5.0;

	out_colour = texture(originalTexture, pass_textureCoords) + vec4(colour, 0.0);
}
