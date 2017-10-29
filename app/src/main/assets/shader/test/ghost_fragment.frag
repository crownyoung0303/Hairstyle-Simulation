precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform float uLandmarkX[106];
uniform float uLandmarkY[106];
uniform int uMouthOpen;


void main() {
    vec2 uv = textureCoordinate;
    vec4 camera = texture2D(vTexture, uv);

    if (uMouthOpen == 0) {
        gl_FragColor = camera;
    } else {
        float purple = camera.r * 0.5 + camera.b * 0.5;
        camera = vec4(purple, camera.g, purple, camera.a);

        float radius = 0.375 * abs(uLandmarkX[104] - uLandmarkX[105]);  // 0.2 ~ 0.4 -> 0.075 ~ 0.15
        vec2 ellipseScale = vec2(0.75, 1.5);

        vec2 center1 = vec2(uLandmarkX[104], uLandmarkY[104]);
        vec2 center2 = vec2(uLandmarkX[105], uLandmarkY[105]);
        vec2 center3 = vec2(uLandmarkX[98], (uLandmarkY[102] + uLandmarkY[98])*0.5);

        float dist1 = length(ellipseScale * (uv - center1));
        float dist2 = length(ellipseScale * (uv - center2));
        float dist3 = length(ellipseScale * (uv - center3));

        vec4 dark = vec4(0.0, 0.0, 0.0, 1.0);

        vec4 tmp = mix(dark, camera, smoothstep(0.0, 1.0, dist1/radius));
        tmp = mix(dark, tmp, smoothstep(0.0, 1.0, dist2/radius));
        gl_FragColor = mix(dark, tmp, smoothstep(0.0, 1.0, dist3/radius));

        float minHeight = 0.03;
        float maxHeight = minHeight + 0.04;

        float topWidth = abs(uLandmarkX[85] - uLandmarkX[89]);
        float wavesPerScreen1 = 16.0 + (1.0 - topWidth) * 10.0;

        float bottomWidth = abs(uLandmarkX[91] - uLandmarkX[95]);
        float wavesPerScreen2 = 16.0 + (1.0 - bottomWidth) * 10.0;

        vec2 topLip = vec2(uLandmarkX[98], uLandmarkY[98]);
        vec2 bottomLip = vec2(uLandmarkX[102], uLandmarkY[102]);
        vec2 pos1 = topLip * 1. - 0.5;
        vec2 pos2 = bottomLip * 1. - 0.5;

        if (uv.x > uLandmarkX[89] && uv.x < uLandmarkX[85]) {
            float translation1 = 0.1 - pos1.x;
            float sinval1 = minHeight + (0.5 + 0.5 * sin((translation1 + uv.x) * wavesPerScreen1*2.0*3.14)) * maxHeight;
            if (uv.y > topLip.y && sinval1 > uv.y - topLip.y + 0.05) {
                gl_FragColor = vec4(0.7, 0.7, 0.7, 1.0);
            }
        }

        if (uv.x > uLandmarkX[101] && uv.x < uLandmarkX[103]) {
            float translation2 = 0.1 - pos2.x;
            float sinval2 = minHeight + (0.5 + 0.5 * sin((translation2 + uv.x) * wavesPerScreen2*2.0*3.14)) * maxHeight;
            if (uv.y < bottomLip.y && sinval2 < uv.y - bottomLip.y + 0.075) {
                gl_FragColor = vec4(0.7, 0.7, 0.7, 1.0);
            }
        }
    }
}