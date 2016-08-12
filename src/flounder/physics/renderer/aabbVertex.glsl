#version 130

layout(location = 0) in vec3 in_position;
layout(location = 1) in vec2 in_textureCoords;
layout(location = 1) in vec3 in_normal;
layout(location = 3) in vec3 in_tangent;

varying vec4 positionRelativeToCam;
varying vec3 surfaceNormal;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 clipPlane;
uniform mat4 modelMatrix;

void main(void) {
    vec4 worldPosition = modelMatrix * vec4(in_position, 1.0);
	mat4 modelViewMatrix = viewMatrix * modelMatrix;
	positionRelativeToCam = modelViewMatrix * vec4(in_position, 1.0);
	surfaceNormal = (modelMatrix * vec4(in_normal, 0.0)).xyz;

	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	gl_Position = projectionMatrix * positionRelativeToCam;
}
