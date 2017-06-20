#version

//---------INCLUDES------------
#include "maths.glsl"
#include "noise.glsl"

//---------CONSTANT------------
const int KERNEL_SIZE = 64;
const float KERNEL_RADIUS = 0.5;

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 1) uniform sampler2D originalNormals;
layout(binding = 3) uniform sampler2D originalDepth;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float aspectRatio;
uniform vec3 kernel[KERNEL_SIZE];
uniform bool enabled;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------CALCULATE LOCATION------------
vec3 decodeLocation() {
    float depth = texture(originalDepth, pass_textureCoords).r;
    vec4 p = finverse(projectionMatrix) * (vec4(pass_textureCoords, depth, 1.0) * 2.0 - 1.0);
    return vec3(finverse(viewMatrix) * vec4(p.xyz / p.w, 1.0));
}

//---------MAIN------------
void main(void) {
    // Reads all of the data passed to this fragment.
	vec4 normals = texture(originalNormals, pass_textureCoords);
	vec3 normal = normals.rgb * 2.0 - 1.0;

	// Ignores anything this is not a rendered object, so mostly the cleared colour.
	if (normals.a == 0.0) {
	    out_colour = vec4(1.0, 1.0, 1.0, 1.0);
	    return;
	}

	vec3 viewRay = normalize(vec4(decodeLocation(), 1.0).xyz);
	float viewDepth = texture(originalDepth, pass_textureCoords).r;
	vec3 fragPos = viewRay * viewDepth;

    // Creating some randomness.
    vec2 rasr = pass_textureCoords * vec2(aspectRatio, 1.0);
    vec3 randomVec = vec3(snoise2(rasr * 3000.0) + 0.3, snoise2(rasr * -3000.0) + 0.3, 0.0);
    randomVec = normalize(randomVec);

	float fragDepth = length(fragPos);
	vec3 tangent = normalize(randomVec - normal * dot(randomVec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 TBN = mat3(tangent, bitangent, normal);  

	float occlusion = 0.0;

	for (int i = 0; i < KERNEL_SIZE; i++) {
		//Get sample position
		vec3 samplePosition = TBN * kernel[i].xyz;
		samplePosition = fragPos + samplePosition * KERNEL_RADIUS;

		vec4 offset = vec4(samplePosition, 1.0);
		offset = projectionMatrix * offset; // from view to clip-space
		offset.xyz /= offset.w; // perspective divide
		offset.xy = offset.xy * 0.5 + 0.5; // transform to range 0.0 - 1.0
		offset.y = 1.0-offset.y;

		float sampleDepth = texture(originalDepth, offset.xy).r;

		float samplerpositionDepth = length(samplePosition);

		//Range check and accumulate
	//	float rangeCheck = smoothstep(0.0, 1.0, KERNEL_RADIUS / abs(fragDepth - sampleDepth));
	//	occlusion += (sampleDepth >= (samplerpositionDepth-0.2) ? 0.0 : 1.0) * rangeCheck;

        // Range check & accumulate:
        float rangeCheck= abs(fragDepth - sampleDepth) < KERNEL_RADIUS ? 1.0 : 0.0;
        occlusion += (sampleDepth <= samplePosition.z ? 1.0 : 0.0) * rangeCheck;
	}

	occlusion = 1.0 - (occlusion / float(KERNEL_SIZE));
    out_colour = vec4(occlusion, occlusion, occlusion, 1.0);

    if (!enabled) {
        out_colour = vec4(1);
    }
}
