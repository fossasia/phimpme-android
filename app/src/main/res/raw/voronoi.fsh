precision highp float;

uniform int                 iFrame;
uniform vec3                iResolution;
uniform vec3                iChannelResolution[2];
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy / iResolution.xy;

#if 0 // debug feature extraction

    fragColor = texture2D(iChannel1, uv).wwww;

#else

	vec4 cell = texture2D(iChannel0, uv);
    vec2 cell_uv = cell.xy;
    vec4 video = texture2D(iChannel1, cell_uv);
    vec2 dcell = cell_uv * iChannelResolution[0].xy - fragCoord.xy;
    float len = length(dcell);
    vec3 color = video.xyz * (.9 + len*.005);
    fragColor = vec4(color, 1.);

#endif
}

void main() {
	mainImage(gl_FragColor, texCoord*iResolution.xy);
}