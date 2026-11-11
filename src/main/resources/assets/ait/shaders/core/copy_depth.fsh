#version 150

uniform sampler2D Sampler0;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    gl_FragDepth = texture(Sampler0, texCoord0).r;
    fragColor = vec4(0.0);
}
