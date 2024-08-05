#version 330


uniform vec2 uResolution;

out vec4 outColor;

uniform sampler2D tex;

void main() {
	vec2 uv = gl_FragCoord.xy/uResolution;	
	
}