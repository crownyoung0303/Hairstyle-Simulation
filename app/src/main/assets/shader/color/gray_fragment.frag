precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;
void main() {
    vec4 color = texture2D( vTexture, textureCoordinate);
    // float rgb = color.g;
    // vec4 c = vec4(rgb, rgb, rgb, color.a);
    // gl_FragColor = c;

    float c = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;
    gl_FragColor = vec4(c, c, c, color.a);
}