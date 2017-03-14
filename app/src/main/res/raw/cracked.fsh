precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

float rnd(vec2 s)
{
    return 1.-2.*fract(sin(s.x*253.13+s.y*341.41)*589.19);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 p=(fragCoord.xy*2.-iResolution.xy)/iResolution.x;

    vec2 v=vec2(1E3);
    vec2 v2=vec2(1E4);
    vec2 center=vec2(.1,-.5);
    for(int c=0;c<30;c++)
    {
        float angle=floor(rnd(vec2(float(c),387.44))*16.)*3.1415*.4-.5;
        float dist=pow(rnd(vec2(float(c),78.21)),2.)*.5;
        vec2 vc=vec2(center.x+cos(angle)*dist+rnd(vec2(float(c),349.3))*7E-3,
                     center.y+sin(angle)*dist+rnd(vec2(float(c),912.7))*7E-3);
        if(length(vc-p)<length(v-p))
        {
	        v2=v;
	        v=vc;
        }
        else if(length(vc-p)<length(v2-p))
        {
            v2=vc;
        }
    }

    float col=abs(length(dot(p-v,normalize(v-v2)))-length(dot(p-v2,normalize(v-v2))))+.002*length(p-center);
    col=7E-4/col;
    if(length(v-v2)<4E-3)col=0.;
//    if(length(v-p)<4E-3)col=1E-6;
    if(col<.3)col=0.;
    vec4 tex=texture2D(iChannel0,(fragCoord.xy)/iResolution.xy+rnd(v)*.02);
    fragColor=col*vec4(vec3(1.-tex.xyz),1.)+(1.-col)*tex;
}

void main() {
	mainImage(gl_FragColor, texCoord * iResolution.xy);
}