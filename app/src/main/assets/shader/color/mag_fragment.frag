precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;
varying vec4 gPosition;
uniform float uXY;

uniform float uScale;  // 0.5

void main() {
    vec4 nColor = texture2D(vTexture, textureCoordinate);
    vec3 vChangeColor = vec3(0.0, 0.0, uScale);
    float dis = distance(vec2(gPosition.x, gPosition.y/uXY), vec2(vChangeColor.r, vChangeColor.g));
    if(dis < vChangeColor.b){
        nColor = texture2D(vTexture, vec2(textureCoordinate.x/2.0 + 0.25, textureCoordinate.y/2.0 + 0.25));
    }
    gl_FragColor = nColor;
}