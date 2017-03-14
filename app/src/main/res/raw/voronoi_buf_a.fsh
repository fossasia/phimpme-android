precision highp float;

uniform int                 iFrame;
uniform vec3                iResolution;
uniform vec3                iChannelResolution[2];
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;

// A super simple video source with feature detection

float grayScale(vec4 c) { return c.x*.29 + c.y*.58 + c.z*.13; }

//============================================================
vec4 GenerateSeed (in vec2 fragCoord)
{
    vec2 uv = fragCoord / iResolution.xy;
    vec3 dataStep = vec3( vec2(1.) / iChannelResolution[0].xy, 0.);

    vec4 fragColor = texture2D( iChannel0, uv );

    float d = grayScale(fragColor);
    float dL = grayScale(texture2D( iChannel0, uv - dataStep.xz ));
    float dR = grayScale(texture2D( iChannel0, uv + dataStep.xz ));
    float dU = grayScale(texture2D( iChannel0, uv - dataStep.zy ));
    float dD = grayScale(texture2D( iChannel0, uv + dataStep.zy ));
    float w = float( d*0.99 > max(max(dL, dR), max(dU, dD)) );

    w = max(w, texture2D( iChannel1, uv ).w*.9); // get some from previous frame

    fragColor.w = w;

    return fragColor;
}

//============================================================
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    fragColor = GenerateSeed(fragCoord);
}


void main() {
	mainImage(gl_FragColor, texCoord*iResolution.xy);
}