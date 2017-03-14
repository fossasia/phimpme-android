precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

// Size of the quad in pixels
const float size = 15.0;

// Radius of the circle
const float radius = size * 0.5;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	// Current quad in pixels
	vec2 quadPos = floor(fragCoord.xy / size) * size;
	// Normalized quad position
	vec2 quad = quadPos/iResolution.xy;
	// Center of the quad
	vec2 quadCenter = (quadPos + size/2.0);
	// Distance to quad center
	float dist = length(quadCenter - fragCoord.xy);

	vec4 texel = texture2D(iChannel0, quad);
	if (dist > radius)
	{
		fragColor = vec4(0.25);
	}
	else
	{
		fragColor = texel;
	}
}


void main() {
	mainImage(gl_FragColor, texCoord*iResolution.xy);
}