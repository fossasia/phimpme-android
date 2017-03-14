precision mediump float;

uniform vec3                iResolution;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

#define S (iResolution.x / 6e1) // The cell size.

void mainImage(out vec4 c, vec2 p)
{
    c = texture2D(iChannel0, floor((p + .5) / S) * S / iResolution.xy);
}

void main() {
	mainImage(gl_FragColor, texCoord*iResolution.xy);
}