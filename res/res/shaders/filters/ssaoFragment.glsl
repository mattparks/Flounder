#version

//---------INCLUDES------------
#include "maths.glsl"
#include "noise.glsl"

//---------CONSTANT------------
const float u_occluderBias = 0.5;
const float u_samplingRadius = 30;
const vec2 u_attenuation = vec2(0.3, 0.36); // .x constant, .y linear, .z quadratic (unused)
const float Sin45 = 0.707107;   // 45 degrees = sin(PI / 4)


//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalAlbedo;
layout(binding = 1) uniform sampler2D originalNormals;
layout(binding = 3) uniform sampler2D originalDepth;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float aspectRatio;
uniform vec2 texelSize;
uniform bool enabled;

//---------OUT------------
layout(location = 0) out vec4 out_colour;

//---------CALCULATE LOCATION------------
vec3 decodeLocation() {
    float depth = texture(originalDepth, pass_textureCoords).r;
    vec4 p = finverse(projectionMatrix) * (vec4(pass_textureCoords, depth, 1.0) * 2.0 - 1.0);
    return vec3(finverse(viewMatrix) * vec4(p.xyz / p.w, 1.0));
}

/// Sample the ambient occlusion at the following UV coordinate.
float SamplePixels(vec3 srcPosition, vec3 srcNormal, vec2 tex_coord)
{
  float dstDepth = texture(originalDepth, tex_coord).r;
  vec4 p = finverse(projectionMatrix) * (vec4(tex_coord, dstDepth, 1.0) * 2.0 - 1.0);
  vec3 dstPosition = vec3(finverse(viewMatrix) * vec4(p.xyz / p.w, 1.0));

  // Calculate ambient occlusion amount between these two points
  // It is simular to diffuse lighting. Objects directly above the fragment cast
  // the hardest shadow and objects closer to the horizon have minimal effect.
  vec3 positionVec = dstPosition - srcPosition;
  float intensity = max(dot(normalize(positionVec), srcNormal) - u_occluderBias, 0.0);

  // Attenuate the occlusion, similar to how you attenuate a light source.
  // The further the distance between points, the less effect AO has on the fragment.
  float dist = length(positionVec);
  float attenuation = 1.0 / (u_attenuation.x + (u_attenuation.y * dist));

  return intensity * attenuation;
}

//---------MAIN------------
void main(void) {
    // Reads all of the data passed to this fragment.
	vec4 albedo = texture(originalAlbedo, pass_textureCoords);
	vec4 normals = vec4(texture(originalNormals, pass_textureCoords).xyz * 2.0 - 1.0, 0.0);

    // Calculates some fragment data.
	float depth = texture(originalDepth, pass_textureCoords).r;
	vec4 worldPosition = vec4(decodeLocation(), 1.0);

    vec2 rasr = pass_textureCoords * vec2(aspectRatio, 1.0);
    vec2 randVec = vec2(snoise2(rasr * 1000.0), snoise2(rasr * -1000.0));
    randVec = normalize(randVec);

    // The following variable specifies how many pixels we skip over after each iteration in the ambient occlusion loop.
    // Pixels far off in the distance will not sample as many pixels as those close up.
    float kernelRadius = u_samplingRadius * (1.0 - depth);

    // Sample neighbouring pixels
    vec2 kernel[4];
    kernel[0] = vec2(0.0, 1.0); // Top.
    kernel[1] = vec2(1.0, 0.0); // Right.
    kernel[2] = vec2(0.0, -1.0); // Bottom.
    kernel[3] = vec2(-1.0, 0.0); // Left.

   // Sample from 16 pixels, which should be enough to appromixate a result.
   float occlusion = 0.0;

   for (int i = 0; i < 4; ++i) {
       vec2 k1 = reflect(kernel[i], randVec);

       vec2 k2 = vec2(k1.x * Sin45 - k1.y * Sin45, k1.x * Sin45 + k1.y * Sin45);

       k1 *= texelSize;
       k2 *= texelSize;

       occlusion += SamplePixels(worldPosition.xyz, normals.xyz, pass_textureCoords + k1 * kernelRadius);
       occlusion += SamplePixels(worldPosition.xyz, normals.xyz, pass_textureCoords + k2 * kernelRadius * 0.75);
       occlusion += SamplePixels(worldPosition.xyz, normals.xyz, pass_textureCoords + k1 * kernelRadius * 0.5);
       occlusion += SamplePixels(worldPosition.xyz, normals.xyz, pass_textureCoords + k2 * kernelRadius * 0.25);
   }

   // Average and clamp ambient occlusion.
   occlusion /= 16;
   occlusion = 1.0 - clamp(occlusion, 0.0, 1.0);

   out_colour = vec4(occlusion, occlusion, occlusion, 1.0);
   if (!enabled) {
        out_colour = vec4(1);
   }
}
