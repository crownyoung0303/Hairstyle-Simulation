precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform float uLandmarkX[106];
uniform float uLandmarkY[106];


vec2 getModifiedPoint(vec2 actualUV, vec2 pointUV, float radius, float strength){
	vec2 vecToPoint = pointUV - actualUV;
	float distToPoint = length(vecToPoint);

	float mag = (1.0 - (distToPoint / radius)) * strength;
	mag *= step(distToPoint, radius);

	return mag * vecToPoint;
}

void main() {
    vec2 uv = textureCoordinate;

    vec2 leftFace = vec2(uLandmarkX[11], uLandmarkY[6]);
    vec2 rightFace = vec2(uLandmarkX[21], uLandmarkY[26]);

    float faceRadius = abs(uLandmarkX[44] - uLandmarkX[3]) * 0.5;
    float faceStrength = 1.0;

    vec2 changePos =
        getModifiedPoint(uv, leftFace, faceRadius, faceStrength) +
        getModifiedPoint(uv, rightFace, faceRadius, faceStrength);

    gl_FragColor = texture2D(vTexture, uv + changePos);
}