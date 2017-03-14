precision highp float;

uniform int                 iFrame;
uniform vec3                iResolution;
uniform vec3                iChannelResolution[2];
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;

// A secondary buffer to get clean Voronoi every N-th frame

// this must be in sync with JFA algorithm constant
const float c_maxSteps = 10.0;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
   	vec2 uv = fragCoord.xy / iResolution.xy;
    if (mod(float(iFrame+1),c_maxSteps) < .5) {
        fragColor = texture2D(iChannel1, uv); // update to new voronoi cell
    } else {
        fragColor = texture2D(iChannel0, uv); // no change
    }
}


void main() {
	mainImage(gl_FragColor, texCoord*iResolution.xy);
}