#version 150

uniform sampler2D Sampler0;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    // Sampler0 is a depth (or packed depth-stencil) texture; its .r channel is the normalised depth. Write it straight
    // to gl_FragDepth so the depth is copied per-fragment - this works across mismatched depth formats where a
    // glBlitFramebuffer(GL_DEPTH_BUFFER_BIT) is rejected (GL_INVALID_OPERATION on Apple's strict GL driver). The colour
    // output is masked off by the caller, so its value is irrelevant.
    gl_FragDepth = texture(Sampler0, texCoord0).r;
    fragColor = vec4(0.0);
}
