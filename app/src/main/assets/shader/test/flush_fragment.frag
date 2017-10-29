precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform float uLandmarkX[106];
uniform float uLandmarkY[106];


void main() {
    vec2 uv = textureCoordinate;
    vec4 camera = texture2D(vTexture, uv);

    float radius = 0.075;
    vec2 scale = vec2(0.75, 1.5);

    vec2 center1 = vec2(uLandmarkX[10], uLandmarkY[6]);
    vec2 center2 = vec2(uLandmarkX[22], uLandmarkY[26]);

    float dist1 = length(scale * (uv - center1));
    float dist2 = length(scale * (uv - center2));

    vec4 flush = vec4(1.0, 0.2, 0.3, 1.0);

    vec4 tmp = mix(flush, camera, smoothstep(0.0, 1.0, dist1/radius));
    gl_FragColor = mix(flush, tmp, smoothstep(0.0, 1.0, dist2/radius));
}