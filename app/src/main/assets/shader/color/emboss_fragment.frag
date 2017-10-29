precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

void main() {
    vec2 TexSize = vec2(100.0, 100.0);
    vec4 bkColor = vec4(0.5, 0.5, 0.5, 1.0);
    vec2 tex = textureCoordinate;

    vec2 upLeftUV = vec2(tex.x - 1.0 / TexSize.x, tex.y - 1.0 / TexSize.y);
    vec4 curColor = texture2D(vTexture, textureCoordinate);
    vec4 upLeftColor = texture2D(vTexture, upLeftUV);
    vec4 delColor = curColor - upLeftColor;
    float h = 0.3 * delColor.x + 0.59 * delColor.y + 0.11 * delColor.z;
    gl_FragColor = vec4(h, h, h, 0.0) + bkColor;
}