#version

//---------INCLUDES------------
#include "maths.glsl"
#include "noise.glsl"

//---------CONSTANT------------
const vec3 SPHERE[16] = vec3[](vec3(0.53812504, 0.18565957, -0.43192),vec3(0.13790712, 0.24864247, 0.44301823),vec3(0.33715037, 0.56794053, -0.005789503),vec3(-0.6999805, -0.04511441, -0.0019965635),vec3(0.06896307, -0.15983082, -0.85477847),vec3(0.056099437, 0.006954967, -0.1843352),vec3(-0.014653638, 0.14027752, 0.0762037),vec3(0.010019933, -0.1924225, -0.034443386),vec3(-0.35775623, -0.5301969, -0.43581226),vec3(-0.3169221, 0.106360726, 0.015860917),vec3(0.010350345, -0.58698344, 0.0046293875),vec3(-0.08972908, -0.49408212, 0.3287904),vec3(0.7119986, -0.0154690035, -0.09183723),vec3(-0.053382345, 0.059675813, -0.5411899),vec3(0.035267662, -0.063188605, 0.54602677),vec3(-0.47761092, 0.2847911, -0.0271716));
const float totStrength = 1.38;
const float strength = 0.05;
const float offset = 18.0;
const float falloff = 0.000002;
const float rad = 0.006;
const int SAMPLES = 16;
const float invSamples = 1.0/16.0;

//---------IN------------
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D originalAlbedo;
layout(binding = 1) uniform sampler2D originalNormals;
layout(binding = 3) uniform sampler2D originalDepth;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float aspectRatio;

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
	vec4 albedo = texture(originalAlbedo, pass_textureCoords);
	vec4 normals = vec4(texture(originalNormals, pass_textureCoords).xyz * 2.0 - 1.0, 0.0);

    // Calculates some fragment data.
	vec4 worldPosition = vec4(decodeLocation(), 1.0);
   float currentPixelDepth = texture(originalDepth, pass_textureCoords).r;

    vec2 rasr = pass_textureCoords * vec2(aspectRatio, 1.0);
  //  vec3 randomVec = vec3(snoise2(rasr * 1000.0), snoise2(rasr * -1000.0), 0.0);
  //  randomVec = normalize(randomVec);
   vec3 fres = normalize((normalize(vec3(snoise2(rasr * 5000.0), snoise2(rasr * -5000.0), 0.0))*2.0) - vec3(1.0));

   // adjust for the depth ( not shure if this is good..)
    float radD = rad / currentPixelDepth;

    vec3 ray, se, occNorm;
    float occluderDepth, depthDifference, normDiff;

   float bl = 0.0;

    for(int i = 0; i < SAMPLES; ++i) {
      // get a vector (randomized inside of a sphere with radius 1.0) from a texture and reflect it
       ray = radD*reflect(SPHERE[i],fres);

      // if the ray is outside the hemisphere then change direction
      se = vec3(pass_textureCoords.xy, currentPixelDepth) + sign(dot(ray,normals.xyz) )*ray;

      // get the normal of the occluder fragment
      occNorm = texture(originalNormals, se.xy).rgb * 2.0 - 1.0;
      occluderDepth = texture(originalDepth, se.xy).r;

      // if depthDifference is negative = occluder is behind current fragment
      depthDifference = currentPixelDepth-occluderDepth;


      // calculate the difference between the normals as a weight

      normDiff = (1.0-dot(occNorm,normals.xyz));
      // the falloff equation, starts at falloff and is kind of 1/x^2 falling
      bl += step(falloff,depthDifference)*normDiff*(1.0-smoothstep(falloff,strength,depthDifference));
    }

   // output the result
   float ao = 1.0-totStrength*bl*invSamples;
   out_colour = vec4(ao, ao, ao, 1.0);
   out_colour = vec4(out_colour.rgb*ao, 1.0);

    //out_colour = vec4(bl, bl, bl, 1.0); // occlusion, occlusion, occlusion
}
