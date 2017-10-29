precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform float uThreshold;  // 0.5

void main() {
    vec4 nColor = texture2D(vTexture, textureCoordinate);
    float avg = (nColor.r + nColor.g + nColor.b) / 3.0;
    float binary;
    if (avg >= uThreshold) {
        binary = 1.0;
    } else {
        binary = 0.0;
    }
    gl_FragColor = vec4(binary, binary, binary, nColor.a);
}