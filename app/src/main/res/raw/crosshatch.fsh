precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

// The brightnesses at which different hatch lines appear
float hatch_1 = 0.8;
float hatch_2 = 0.6;
float hatch_3 = 0.3;
float hatch_4 = 0.15;

// How close together hatch lines should be placed
float density = 10.0;

// How wide hatch lines are drawn.
float width = 1.0;

// enable GREY_HATCHES for greyscale hatch lines
#define GREY_HATCHES

// enable COLOUR_HATCHES for coloured hatch lines
#define COLOUR_HATCHES

#ifdef GREY_HATCHES
float hatch_1_brightness = 0.8;
float hatch_2_brightness = 0.6;
float hatch_3_brightness = 0.3;
float hatch_4_brightness = 0.0;
#else
float hatch_1_brightness = 0.0;
float hatch_2_brightness = 0.0;
float hatch_3_brightness = 0.0;
float hatch_4_brightness = 0.0;
#endif

float d = 1.0; // kernel offset

float lookup(vec2 p, float dx, float dy)
{
    vec2 uv = (p.xy + vec2(dx * d, dy * d)) / iResolution.xy;
    vec4 c = texture2D(iChannel0, uv.xy);

	// return as luma
    return 0.2126*c.r + 0.7152*c.g + 0.0722*c.b;
}


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	//
	// Inspired by the technique illustrated at
	// http://www.geeks3d.com/20110219/shader-library-crosshatching-glsl-filter/
	//
	float ratio = iResolution.y / iResolution.x;
	float coordX = fragCoord.x / iResolution.x;
	float coordY = fragCoord.y / iResolution.x;
	vec2 dstCoord = vec2(coordX, coordY);
	vec2 srcCoord = vec2(coordX, coordY / ratio);
	vec2 uv = srcCoord.xy;

	vec3 res = vec3(1.0, 1.0, 1.0);
    vec4 tex = texture2D(iChannel0, uv);
    float brightness = (0.2126*tex.x) + (0.7152*tex.y) + (0.0722*tex.z);
#ifdef COLOUR_HATCHES
	// check whether we have enough of a hue to warrant coloring our
	// hatch strokes.  If not, just use greyscale for our hatch color.
	float dimmestChannel = min( min( tex.r, tex.g ), tex.b );
	float brightestChannel = max( max( tex.r, tex.g ), tex.b );
	float delta = brightestChannel - dimmestChannel;
	if ( delta > 0.1 )
		tex = tex * ( 1.0 / brightestChannel );
	else
		tex.rgb = vec3(1.0,1.0,1.0);
#endif // COLOUR_HATCHES

    if (brightness < hatch_1)
    {
		if (mod(fragCoord.x + fragCoord.y, density) <= width)
		{
#ifdef COLOUR_HATCHES
			res = vec3(tex.rgb * hatch_1_brightness);
#else
			res = vec3(hatch_1_brightness);
#endif
		}
    }

    if (brightness < hatch_2)
    {
		if (mod(fragCoord.x - fragCoord.y, density) <= width)
		{
#ifdef COLOUR_HATCHES
			res = vec3(tex.rgb * hatch_2_brightness);
#else
			res = vec3(hatch_2_brightness);
#endif
		}
    }

    if (brightness < hatch_3)
    {
		if (mod(fragCoord.x + fragCoord.y - (density*0.5), density) <= width)
		{
#ifdef COLOUR_HATCHES
			res = vec3(tex.rgb * hatch_3_brightness);
#else
			res = vec3(hatch_3_brightness);
#endif
		}
    }

    if (brightness < hatch_4)
    {
		if (mod(fragCoord.x - fragCoord.y - (density*0.5), density) <= width)
		{
#ifdef COLOUR_HATCHES
			res = vec3(tex.rgb * hatch_4_brightness);
#else
			res = vec3(hatch_4_brightness);
#endif
		}
    }

	vec2 p = fragCoord.xy;

	// simple sobel edge detection,
	// borrowed and tweaked from jmk's "edge glow" filter, here:
	// https://www.shadertoy.com/view/Mdf3zr
    float gx = 0.0;
    gx += -1.0 * lookup(p, -1.0, -1.0);
    gx += -2.0 * lookup(p, -1.0,  0.0);
    gx += -1.0 * lookup(p, -1.0,  1.0);
    gx +=  1.0 * lookup(p,  1.0, -1.0);
    gx +=  2.0 * lookup(p,  1.0,  0.0);
    gx +=  1.0 * lookup(p,  1.0,  1.0);

    float gy = 0.0;
    gy += -1.0 * lookup(p, -1.0, -1.0);
    gy += -2.0 * lookup(p,  0.0, -1.0);
    gy += -1.0 * lookup(p,  1.0, -1.0);
    gy +=  1.0 * lookup(p, -1.0,  1.0);
    gy +=  2.0 * lookup(p,  0.0,  1.0);
    gy +=  1.0 * lookup(p,  1.0,  1.0);

	// hack: use g^2 to conceal noise in the video
    float g = gx*gx + gy*gy;
	res *= (1.0-g);

	fragColor = vec4(res, 1.0);
}

void main() {
	mainImage(gl_FragColor, texCoord * iResolution.xy);
}