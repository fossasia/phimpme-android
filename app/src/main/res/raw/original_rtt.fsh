#extension GL_OES_EGL_image_external : require
precision mediump float;

varying vec2                texCoord;
uniform samplerExternalOES  iChannel0;

void main() {
    gl_FragColor = texture2D(iChannel0, texCoord);
}