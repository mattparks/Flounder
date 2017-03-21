#version 130

//---------IN------------
layout(location = 0) in vec2 in_position;
layout(location = 1) in vec2 in_textureCoords;

//---------UNIFORM------------
uniform vec2 size;
uniform vec4 transform;
uniform float rotation;

//---------OUT------------
out vec2 pass_textureCoords;

//---------MAIN------------
void main(void) {
	vec2 screenPosition = vec2(
		(in_position.x - size.x) * cos(rotation) - (in_position.y - size.y) * sin(rotation),
		(in_position.x - size.x) * sin(rotation) + (in_position.y - size.y) * cos(rotation)
	);
	screenPosition = screenPosition * transform.zw + transform.xy;
	screenPosition.x = screenPosition.x * 2.0 - 1.0;
	screenPosition.y = screenPosition.y * -2.0 + 1.0;
	gl_Position = vec4(screenPosition, 0.0, 1.0);

	// gl_Position = vec4(in_position * transform.zw + transform.xy * vec2(2.0, -2.0), 0.0, 1.0);

	pass_textureCoords = in_textureCoords;
}
