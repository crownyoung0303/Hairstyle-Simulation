precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

void main() {
//    vec4 nColor = texture2D(vTexture, textureCoordinate);
//    gl_FragColor = vec4(1.0 - nColor.r, 1.0 - nColor.g, 1.0 - nColor.b, nColor.a);

    vec2 p = textureCoordinate - 0.5;

    // cartesian to polar coordinates
    float r = length(p);
    float a = atan(p.y, p.x);

    // distort
    //r = sqrt(r)*0.3; // pinch
    r = r*r * 3.0; // bulge

    // polar to cartesian coordinates
    p = r * vec2(cos(a)*0.5, sin(a)*0.5);

    // sample the iChannel0
    vec4 color = texture2D(vTexture, p + 0.5);
    gl_FragColor = color;
}