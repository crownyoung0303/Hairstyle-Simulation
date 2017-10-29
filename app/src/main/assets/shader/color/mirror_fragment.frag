precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

void main() {
    vec4 nColor1 = texture2D(vTexture, textureCoordinate);
    vec2 mirrorCoordinate = vec2(1.0 - textureCoordinate.x, textureCoordinate.y);
    vec4 nColor2 = texture2D(vTexture, mirrorCoordinate);
    if (textureCoordinate.x <= 0.5) {
        gl_FragColor = nColor1;
    } else {
        gl_FragColor = nColor2;
    }
}