#version 130

in vec2 pass_textureCoords;

layout(location = 0) out vec4 out_colour;

layout(binding = 0) uniform sampler2D originalTexture;
layout(binding = 1) uniform sampler2D otherTexture;
uniform vec4 slideSpace;

void main(void) {
   if (slideSpace.x <= pass_textureCoords.x && slideSpace.y >= pass_textureCoords.x && slideSpace.z <= pass_textureCoords.y && slideSpace.w >= pass_textureCoords.y) {
       out_colour = texture(originalTexture, pass_textureCoords);
   } else {
       out_colour = texture(otherTexture, pass_textureCoords);
   }
}
