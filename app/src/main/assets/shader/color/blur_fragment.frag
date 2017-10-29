precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;
void main() {
    vec4 nColor = texture2D(vTexture, textureCoordinate);
    vec3 vChangeColor = vec3(0.006, 0.004, 0.002);
    nColor += texture2D(vTexture,vec2(textureCoordinate.x - vChangeColor.r, textureCoordinate.y - vChangeColor.r));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x - vChangeColor.r, textureCoordinate.y + vChangeColor.r));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x + vChangeColor.r, textureCoordinate.y - vChangeColor.r));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x + vChangeColor.r, textureCoordinate.y + vChangeColor.r));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x - vChangeColor.g, textureCoordinate.y - vChangeColor.g));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x - vChangeColor.g, textureCoordinate.y + vChangeColor.g));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x + vChangeColor.g, textureCoordinate.y - vChangeColor.g));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x + vChangeColor.g, textureCoordinate.y + vChangeColor.g));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x - vChangeColor.b, textureCoordinate.y - vChangeColor.b));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x - vChangeColor.b, textureCoordinate.y + vChangeColor.b));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x + vChangeColor.b, textureCoordinate.y - vChangeColor.b));
    nColor += texture2D(vTexture,vec2(textureCoordinate.x + vChangeColor.b, textureCoordinate.y + vChangeColor.b));
    nColor /= 13.0;
    gl_FragColor = nColor;
}