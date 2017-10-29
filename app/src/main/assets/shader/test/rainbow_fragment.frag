precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform float uLandmarkX[106];
uniform float uLandmarkY[106];
uniform int uMouthOpen;
uniform float iGlobalTime;


vec4 rgb(float a, float b, float c) {
    return vec4(a/255.0, b/255.0, c/255.0, 1.0);
}

vec4 getRainbow(vec2 uv, float wave, float width, float startY, float shadowY) {
    vec4 color;
    if (uMouthOpen == 1 && uv.y >= startY) {

        if (wave >= -width*7. && wave < -width*5.) color = rgb(255.0, 0.0, 0.0);

        else if (wave >= -width*5. && wave < -width*3.) color = rgb(255.0, 165.0, 0.0);

        else if (wave >= -width*3. && wave < -width) color = rgb(255.0, 255.0, 0.0);

        else if (wave >= -width && wave < width) color = rgb(0.0, 255.0, 0.0);

        else if (wave >= width && wave < width*3.) color = rgb(0.0, 127.0, 255.0);

        else if (wave >= width*3. && wave < width*5.) color = rgb(0.0, 0.0, 255.0);

        else if (wave >= width*5. && wave < width*7.) color = rgb(139.0, 0.0, 255.0);

        else color = texture2D( vTexture, uv );

        if (wave >= -width*7. && wave < width*7. && uv.y < shadowY) {
            color = color - vec4(0.3, 0.3, 0.3, 1.0);
        }

    } else {
        color = texture2D( vTexture, uv );
    }

    return color;
}

void mainImage( out vec4 O, in vec2 U ) {
    vec2 R = vec2(720.0, 1280.0);
    vec2 uv = U.xy / R.xy;
    U = 10.* (U-R/2.) / R.x;

    float width = abs(uLandmarkX[99] - uLandmarkX[97]);
    vec2 pos = vec2(uLandmarkX[98], (uLandmarkY[96] + uLandmarkY[100])*0.5);
    vec2 vecToPoint = pos - uv;

    float amp = 0.1;
    float wave = amp * sin(U.y- iGlobalTime*10.) -U.x + (pos.x * 2. -1.)* 5.;

    O = getRainbow(uv, wave, width, pos.y, (uLandmarkY[101] + uLandmarkY[103])*0.5);
}

void main() {
    vec2 iResolution = vec2(720.0, 1280.0);
    mainImage(gl_FragColor, textureCoordinate * iResolution.xy);
}