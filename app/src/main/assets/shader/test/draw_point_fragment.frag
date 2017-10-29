precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

void main() {
    vec4 nColor = texture2D(vTexture, textureCoordinate);

    vec2 pos = mod(gl_FragCoord.xy, vec2(50.0)) - vec2(25.0);
    float dist_squared = dot(pos, pos);

    gl_FragColor = (dist_squared < 100.0)
        ? vec4(.90, .90, .90, 1.0)
        // : vec4(.20, .20, .40, 1.0);
        : nColor;
}