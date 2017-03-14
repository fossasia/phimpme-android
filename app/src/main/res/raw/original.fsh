precision mediump float;

varying vec2                texCoord;
uniform sampler2D           iChannel0;

void main() {
    gl_FragColor = texture2D(iChannel0, texCoord);
}