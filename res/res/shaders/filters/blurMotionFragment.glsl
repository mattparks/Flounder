#version 130

//---------INCLUDES------------
#include "maths.glsl"

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalTexture;
layout(binding = 1) uniform sampler2D originalDepth;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lastViewMatrix;
uniform float delta;

const float numSamples = 8.0;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------CALCULATE LOCATION------------
vec3 decodeLocation() {
    float depth = texture(originalDepth, pass_textureCoords).x;
    vec4 p = inverse(projectionMatrix) * (vec4(pass_textureCoords, depth, 1.0) * 2.0 - 1.0);
    return vec3(inverse(viewMatrix) * vec4(p.xyz / p.w, 1.0));
}

//---------MAIN------------
void main(void) {
	//vec3 colour = texture(originalTexture, pass_textureCoords).rgb;

	vec4 worldPosition = vec4(decodeLocation(), 1.0);

    // Transform by the view-projection inverse to get the current world position.
    vec4 currentPos = projectionMatrix * viewMatrix * worldPosition;

    // Use the world position, and transform by the previous view-projection matrix.
    vec4 previousPos = projectionMatrix * lastViewMatrix * worldPosition;

    // Use this frame's position and last frame's to compute the pixel velocity.
    vec2 velocity = (currentPos.xy - previousPos.xy) * 0.05 * delta;

    vec2 texcoord = pass_textureCoords;

    for (float i = 1.0; i < numSamples; ++i){
        out_colour += texture(originalTexture, texcoord);
        texcoord += velocity;
    }

    out_colour = vec4(out_colour.rgb / numSamples, 1.0);
}
