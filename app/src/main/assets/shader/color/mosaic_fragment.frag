precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform vec2 uMosaicSize;  // vec2(8.0, 8.0)


void main() {
    vec2 TexSize = vec2(400.0, 400.0);
    vec2 intXY = vec2(textureCoordinate.x * TexSize.x, textureCoordinate.y * TexSize.y);
    vec2 XYMosaic = vec2(floor(intXY.x / uMosaicSize.x) * uMosaicSize.x, floor(intXY.y / uMosaicSize.y) * uMosaicSize.y);
    vec2 UVMosaic = vec2(XYMosaic.x / TexSize.x, XYMosaic.y / TexSize.y);
    vec4 color = texture2D(vTexture, UVMosaic);
    gl_FragColor = color;
}