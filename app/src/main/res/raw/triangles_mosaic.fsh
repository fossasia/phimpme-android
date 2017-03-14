precision highp float;

uniform vec3                iResolution;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

vec2 tile_num = vec2(40.0,20.0);

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = fragCoord.xy;
	vec2 uv2 = floor(uv*tile_num)/tile_num;
    uv -= uv2;
    uv *= tile_num;
	fragColor = texture2D( iChannel0, uv2 + vec2(step(1.0-uv.y,uv.x)/(2.0*tile_num.x),
        											//0,
        											step(uv.x,uv.y)/(2.0*tile_num.y)
                                                    //0
                                                   ) );
}

void main() {
	mainImage(gl_FragColor, texCoord);
}