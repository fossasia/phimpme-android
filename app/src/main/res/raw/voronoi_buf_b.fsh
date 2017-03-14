precision highp float;

uniform int                 iFrame;
uniform vec3                iResolution;
uniform vec3                iChannelResolution[2];
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;


// how many JFA steps to do.  2^c_maxSteps is max image size on x and y
const float c_maxSteps = 10.0;

//============================================================
vec4 StepJFA (in vec2 fragCoord, in float level)
{
    float stepwidth = floor(exp2(c_maxSteps - 1. - level)+0.5);

    float bestDistance = 9999.0;
    vec2 bestCoord = vec2(0.0);

    for (int y = -1; y <= 1; ++y) {
        for (int x = -1; x <= 1; ++x) {
            vec2 sampleCoord = fragCoord + vec2(x,y) * stepwidth;

            vec4 data = texture2D( iChannel0, sampleCoord / iChannelResolution[0].xy);
            vec2 seedCoord = data.xy * iChannelResolution[0].xy;
            float dist = length(seedCoord - fragCoord);
            if ((seedCoord.x != 0.0 || seedCoord.y != 0.0) && dist < bestDistance)
            {
                bestDistance = dist;
                bestCoord = seedCoord;
            }
        }
    }

    return vec4(bestCoord / iChannelResolution[0].xy, 0.0, 0.0);
}

//============================================================
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    float fFrame = float(iFrame);
    float level = mod(fFrame,c_maxSteps);
    if (level < .5) {
        if (texture2D(iChannel1, fragCoord / iResolution.xy).w > .5)
        	fragColor = vec4(fragCoord / iChannelResolution[0].xy, 0.0, 0.0);
        else
            fragColor = vec4(0.0);
        return;
    }

    fragColor = StepJFA(fragCoord, level);
}


void main() {
	mainImage(gl_FragColor, texCoord*iResolution.xy);
}