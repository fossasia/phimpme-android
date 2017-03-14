attribute vec2  vPosition;
attribute vec2  vTexCoord;
varying vec2    texCoord;

void main() {
    texCoord = vTexCoord;
    gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );
}