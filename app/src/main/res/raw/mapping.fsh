precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           iChannel0;
uniform sampler2D           iChannel1;
varying vec2                texCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{

	vec2 pos = fragCoord.xy;
	vec2 uv2 = vec2( fragCoord.xy / iResolution.xy );
	vec4 sound = texture2D( iChannel0, uv2 );
	pos.x = pos.x + 150.0 * sound.r;
	pos.y = pos.y + 150.0 * sound.b;
	vec2 uv = pos / iResolution.xy;

	vec4 col = texture2D( iChannel1, uv );

	col.a += 1.0 - sin( col.x - col.y + iGlobalTime * 0.1 );

	fragColor =  col * sound.r;
}

void main() {
	mainImage(gl_FragColor, texCoord * iResolution.xy);
}