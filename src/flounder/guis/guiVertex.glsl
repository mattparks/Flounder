#version 130

layout(location = 0) in vec2 in_position;

varying vec2 pass_textureCoords;

uniform vec4 transform;
uniform float flipTexture;
uniform float atlasRows;
uniform vec2 atlasOffset;

void main(void) {
	pass_textureCoords = in_position;
	pass_textureCoords = (pass_textureCoords / atlasRows) + atlasOffset;
	pass_textureCoords.y = mix(pass_textureCoords.y, 1.0 - pass_textureCoords.y, flipTexture);
	vec2 screenPosition = in_position * transform.zw + transform.xy;
	screenPosition.x = screenPosition.x * 2.0 - 1.0;
	screenPosition.y = screenPosition.y * -2.0 + 1.0;
	gl_Position = vec4(screenPosition, 0.0, 1.0);
}
