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
    float radius = abs(uLandmarkY[72] - uLandmarkY[73]) * 2.0;
	float strength = 1.0;

	vec2 pos1 = vec2(uLandmarkX[74], uLandmarkY[74]);
    vec2 pos2 = vec2(uLandmarkX[77], uLandmarkY[77]);

    vec2 changePos =
        getModifiedPoint(uv, pos1, radius, strength) +
        getModifiedPoint(uv, pos2, radius, strength);

    gl_FragColor = texture2D(vTexture, uv + changePos);
}