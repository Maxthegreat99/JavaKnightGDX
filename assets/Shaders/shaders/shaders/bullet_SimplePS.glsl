#version 330

in vec4 color;
out vec4 outColor;

uniform sampler2D tex;

uniform mat4 projMat;
uniform mat4 geoMat;

void main() {
   outColor = vec4(texture(tex, (gl_FragCoord / 128 ).xy).xyz, 1.0);
}