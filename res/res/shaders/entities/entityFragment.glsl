#version 130

//---------IN------------
flat in vec3 pass_surfaceNormal;
in vec2 pass_textureCoords;

//---------UNIFORM------------
layout(binding = 0) uniform sampler2D diffuseMap;
layout(binding = 1) uniform sampler2D glowMap;

//---------OUT------------
layout(location = 0) out vec4 out_albedo;
layout(location = 1) out vec4 out_normals;
layout(location = 2) out vec4 out_extras;

uniform vec3 colourOffset;

uniform float shineDamper;
uniform float reflectivity;

uniform bool ignoreFog;
uniform bool ignoreLighting;

uniform bool useGlowMap;

//---------MAIN------------
void main(void) {
	vec4 diffuseColour = texture(diffuseMap, pass_textureCoords);

	if (diffuseColour.a < 0.1){
		out_albedo = vec4(0.0);
	    out_normals = vec4(0.0);
	    out_extras = vec4(0.0);
		discard;
	}

	bool glowing = false;

	if (useGlowMap) {
	    vec4 glowColour = texture(glowMap, pass_textureCoords);

	    if (glowColour.r > 0.5) {
	        glowing = true;
	    }
	}

	out_albedo = vec4(diffuseColour + vec4(colourOffset, 0.0));
	out_normals = vec4(pass_surfaceNormal, 1.0);
	out_extras = vec4(shineDamper, reflectivity, (1.0 / 3.0) * (float(ignoreFog) + (2.0 * float(ignoreLighting || glowing))), 1.0);
}