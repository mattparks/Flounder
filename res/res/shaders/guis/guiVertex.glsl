#version 130

//---------IN------------
layout(location = 0) in vec2 in_position;

//---------UNIFORM------------
uniform float size;

uniform vec4 transform;
uniform float rotation;
uniform bool flipTexture;
uniform float atlasRows;
uniform vec2 atlasOffset;

//---------OUT------------
out vec2 pass_textureCoords;

//---------MAIN------------
void main(void) {
	pass_textureCoords = in_position;
	pass_textureCoords = (pass_textureCoords / atlasRows) + atlasOffset;
	pass_textureCoords.x = mix(pass_textureCoords.x, 1.0 - pass_textureCoords.x, flipTexture);

    vec2 screenPosition = vec2(
        (in_position.x - size) * cos(rotation) - (in_position.y - size) * sin(rotation),
        (in_position.x - size) * sin(rotation) + (in_position.y - size) * cos(rotation)
    );
    screenPosition = screenPosition * transform.zw + transform.xy;

	screenPosition.x = screenPosition.x * 2.0 - 1.0;
	screenPosition.y = screenPosition.y * -2.0 + 1.0;
	gl_Position = vec4(screenPosition.xy, 0.0, 1.0);
}
