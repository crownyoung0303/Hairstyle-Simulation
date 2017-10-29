precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

void main() {
    vec4 nColor = texture2D(vTexture, textureCoordinate);
    gl_FragColor = vec4(1.0 - nColor.r, 1.0 - nColor.g, 1.0 - nColor.b, nColor.a);
}