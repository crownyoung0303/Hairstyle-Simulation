precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform vec3 uLevel;  // vec3(0.4, 0.4, 0.0)


void modifyColor(vec4 color){
    color.r = max(min(color.r, 1.0), 0.0);
    color.g = max(min(color.g, 1.0), 0.0);
    color.b = max(min(color.b, 1.0), 0.0);
    color.a = max(min(color.a, 1.0), 0.0);
}

void main() {
    vec4 nColor = texture2D(vTexture, textureCoordinate);
    vec3 vChangeColor = vec3(uLevel.x, uLevel.y, uLevel.z);
    vec4 deltaColor = nColor + vec4(vChangeColor, 0.0);
    modifyColor(deltaColor);
    gl_FragColor = deltaColor;
}