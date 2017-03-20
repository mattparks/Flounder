#version 130

//---------IN------------
layout(location = 0) in vec2 in_position;
layout(location = 1) in vec2 in_textureCoords;

//---------UNIFORM------------
uniform vec2 transform;

//---------OUT------------
out vec2 pass_textureCoords;

//---------MAIN------------
void main(void) {
	gl_Position = vec4(in_position + transform * vec2(2.0, -2.0), 0.0, 1.0);
	pass_textureCoords = in_textureCoords;
}
