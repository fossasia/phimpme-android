precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

vec2 hash2( vec2 p )
{
    // procedural white noise
	return fract(sin(vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))))*43758.5453);
}

vec2 voronoi( in vec2 x )
{
    vec2 n = floor(x);
    vec2 f = fract(x);

    //----------------------------------
    // regular voronoi
    //----------------------------------
	vec2 mg, mr;

    float md = 8.0;
    for( int j=-1; j<=1; j++ )
    for( int i=-1; i<=1; i++ )
    {
        vec2 g = vec2(float(i),float(j));
		vec2 o = hash2( n + g );
        vec2 r = g + o - f;
        float d = dot(r,r);

        if( d<md )
        {
            md = d;
            mr = r;
            mg = g;
        }
    }

    return mr;
}

vec3 VoronoiColor(float steps, vec2 p, vec2 uv)
{
    vec2 c = voronoi( steps*p );

    vec2 uv1 = uv;
    uv1.x += c.x/steps;
    uv1.y += c.y/steps *  iResolution.x/iResolution.y;

    return texture2D(iChannel0, vec2(uv1.x, uv1.y)).xyz;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 p = fragCoord.xy/iResolution.xx;
    vec2 uv = fragCoord.xy / iResolution.xy;

    vec3 color = vec3(0.0,0.0,0.0);
    for (float i=0.0; i<4.0; i+=1.0)
    {
        float steps = 30.0*pow(2.0,i);
        color += VoronoiColor(steps, p, uv);
    }

	fragColor = vec4(color*0.25,1.0);
}

void main() {
	mainImage(gl_FragColor, texCoord * iResolution.xy);
}