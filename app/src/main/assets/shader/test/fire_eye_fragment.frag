precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform float uLandmarkX[106];
uniform float uLandmarkY[106];
uniform float iGlobalTime;


vec2 hash( vec2 p ) {
	p = vec2( dot(p,vec2(127.1,311.7)),
			 dot(p,vec2(269.5,183.3)) );
	return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}

float noise( in vec2 p ) {
	const float K1 = 0.366025404; // (sqrt(3)-1)/2;
	const float K2 = 0.211324865; // (3-sqrt(3))/6;

	vec2 i = floor( p + (p.x+p.y)*K1 );

	vec2 a = p - i + (i.x+i.y)*K2;
	vec2 o = (a.x>a.y) ? vec2(1.0,0.0) : vec2(0.0,1.0);
	vec2 b = a - o + K2;
	vec2 c = a - 1.0 + 2.0*K2;

	vec3 h = max( 0.5-vec3(dot(a,a), dot(b,b), dot(c,c) ), 0.0 );

	vec3 n = h*h*h*h*vec3( dot(a,hash(i+0.0)), dot(b,hash(i+o)), dot(c,hash(i+1.0)));

	return dot( n, vec3(70.0) );
}

float fbm(vec2 uv) {
	float f;
	mat2 m = mat2( 1.6,  1.2, -1.2,  1.6 );
	f  = 0.5000*noise( uv ); uv = m*uv;
	f += 0.2500*noise( uv ); uv = m*uv;
	f += 0.1250*noise( uv ); uv = m*uv;
	f += 0.0625*noise( uv ); uv = m*uv;
	f = 0.5 + 0.5*f;
	return f;
}

vec4 drawFireEye(vec2 uv, vec2 q1, vec2 q2, float strength, float T3) {
    float n = fbm(strength*q1 - vec2(0,T3));
    float nm = n * max( 0., q1.y + 0.1 );
    vec2 qv = vec2(1.8 + q1.y * 1.5, .75);
    float width = 320.0 * (1.0 - abs(uLandmarkX[104] - uLandmarkX[105])) - 170.0;  // 0.2 ~ 0.4 -> 0.6 ~ 0.8 -> 192 ~ 256 -> 22 ~ 86
    if (width < 5.0) width = 5.0;

    float f1 = 1.0 - width * pow( max( 0., length(q1 * qv ) - nm ), 1.2 );
    float c1 = clamp(f1, 0., 1.);

    float f2 = 1.0 - width * pow( max( 0., length(q2 * qv ) - nm ), 1.2 );
    float c2 = clamp(f2, 0., 1.);

    vec3 col1 = vec3(1.5*c1, 1.5*c1*c1*c1, c1*c1*c1*c1*c1*c1);
    vec3 col2 = vec3(1.5*c2, 1.5*c2*c2*c2, c2*c2*c2*c2*c2*c2);

    float a = (1.- f1 - f2) * (1.-pow(uv.y, 3.));
    return vec4(mix(vec3(0.), col1 + col2, a), 1.0);
}

void main() {
    vec2 uv = textureCoordinate;
    vec4 camera = texture2D(vTexture, uv);

    vec2 q = vec2(uv.x, 1.-uv.y);
    q.x *= 1.;
    q.y *= 1.;
    float strength = floor(q.x+3.);
    float T3 = max(3.,1.25*strength)*iGlobalTime;

    vec2 pos1 = vec2(uLandmarkX[104], uLandmarkY[65]);
    vec2 q1 = vec2(q.x, q.y);
    q1.x = mod(q.x,1.)-pos1.x;
    q1.y -= (1.-pos1.y);

    vec2 pos2 = vec2(uLandmarkX[105], uLandmarkY[70]);
    vec2 q2 = vec2(q.x, q.y);
    q2.x = mod(q.x,1.)-pos2.x;
    q2.y -= (1.-pos2.y);

    gl_FragColor = camera
                   + drawFireEye(uv, q1, q2, strength, T3);
}