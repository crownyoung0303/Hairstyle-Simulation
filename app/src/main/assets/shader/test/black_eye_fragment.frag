precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform float uLandmarkX[106];
uniform float uLandmarkY[106];


void main() {
    vec2 uv = textureCoordinate;
    vec4 camera = texture2D(vTexture, uv);

    float radius = 0.375 * abs(uLandmarkX[104] - uLandmarkX[105]);  // 0.2 ~ 0.4 -> 0.075 ~ 0.15
    vec2 scale = vec2(0.75, 1.5);

    vec2 center1 = vec2(uLandmarkX[104], uLandmarkY[104]);
    vec2 center2 = vec2(uLandmarkX[105], uLandmarkY[105]);

    float dist1 = length(scale * (uv - center1));
    float dist2 = length(scale * (uv - center2));

    vec4 flush = vec4(0.0, 0.0, 0.0, 1.0);

    vec4 tmp = mix(flush, camera, smoothstep(0.0, 1.0, dist1/radius));
    gl_FragColor = mix(flush, tmp, smoothstep(0.0, 1.0, dist2/radius));
}