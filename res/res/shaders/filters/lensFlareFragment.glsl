#version 130

in vec2 pass_textureCoords;

layout(location = 0) out vec4 out_colour;

layout(binding = 0) uniform sampler2D originalTexture;
uniform vec2 cameraPosition;

void main(void) {
    out_colour = texture(originalTexture, pass_textureCoords);
    // TODO: Make effects!
}
