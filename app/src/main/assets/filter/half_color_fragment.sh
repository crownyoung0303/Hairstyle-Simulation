precision mediump float;

uniform sampler2D vTexture;
uniform int vChangeType;
uniform vec3 vChangeColor;
uniform int vIsHalf;
uniform float uXY;

varying vec4 gPosition;

varying vec2 aCoordinate;
varying vec4 aPos;


void modifyColor(vec4 color){
    color.r = max(min(color.r, 1.0), 0.0);
    color.g = max(min(color.g, 1.0), 0.0);
    color.b = max(min(color.b, 1.0), 0.0);
    color.a = max(min(color.a, 1.0), 0.0);
}

void main(){
    vec4 nColor = texture2D(vTexture, aCoordinate);
    if(aPos.x > 0.0 || vIsHalf == 0) {
        if (vChangeType == 1) {
            float c = nColor.r * vChangeColor.r + nColor.g * vChangeColor.g + nColor.b * vChangeColor.b;
            gl_FragColor = vec4(c, c, c, nColor.a);

        } else if (vChangeType == 2) {
            vec4 deltaColor = nColor + vec4(vChangeColor, 0.0);
            modifyColor(deltaColor);
            gl_FragColor = deltaColor;

        } else if (vChangeType == 3) {
            nColor += texture2D(vTexture,vec2(aCoordinate.x - vChangeColor.r, aCoordinate.y - vChangeColor.r));
            nColor += texture2D(vTexture,vec2(aCoordinate.x - vChangeColor.r, aCoordinate.y + vChangeColor.r));
            nColor += texture2D(vTexture,vec2(aCoordinate.x + vChangeColor.r, aCoordinate.y - vChangeColor.r));
            nColor += texture2D(vTexture,vec2(aCoordinate.x + vChangeColor.r, aCoordinate.y + vChangeColor.r));
            nColor += texture2D(vTexture,vec2(aCoordinate.x - vChangeColor.g, aCoordinate.y - vChangeColor.g));
            nColor += texture2D(vTexture,vec2(aCoordinate.x - vChangeColor.g, aCoordinate.y + vChangeColor.g));
            nColor += texture2D(vTexture,vec2(aCoordinate.x + vChangeColor.g, aCoordinate.y - vChangeColor.g));
            nColor += texture2D(vTexture,vec2(aCoordinate.x + vChangeColor.g, aCoordinate.y + vChangeColor.g));
            nColor += texture2D(vTexture,vec2(aCoordinate.x - vChangeColor.b, aCoordinate.y - vChangeColor.b));
            nColor += texture2D(vTexture,vec2(aCoordinate.x - vChangeColor.b, aCoordinate.y + vChangeColor.b));
            nColor += texture2D(vTexture,vec2(aCoordinate.x + vChangeColor.b, aCoordinate.y - vChangeColor.b));
            nColor += texture2D(vTexture,vec2(aCoordinate.x + vChangeColor.b, aCoordinate.y + vChangeColor.b));
            nColor /= 13.0;
            gl_FragColor = nColor;

        } else if (vChangeType == 4) {
            float dis = distance(vec2(gPosition.x, gPosition.y/uXY), vec2(vChangeColor.r, vChangeColor.g));
            if(dis < vChangeColor.b){
                nColor = texture2D(vTexture, vec2(aCoordinate.x/2.0 + 0.25, aCoordinate.y/2.0 + 0.25));
            }
            gl_FragColor = nColor;

        } else if (vChangeType == 5) {
            float avg = (nColor.r + nColor.g + nColor.b) / 3.0;
            float binary;
            if (avg >= 0.5) {
                binary = 1.0;
            } else {
                binary = 0.0;
            }
            gl_FragColor = vec4(binary, binary, binary, nColor.a);

        } else if (vChangeType == 6) {
            gl_FragColor = vec4(1.0 - nColor.r, 1.0 - nColor.g, 1.0 - nColor.b, nColor.a);

        } else if (vChangeType == 7) {
            vec2 TexSize = vec2(100.0, 100.0);
            vec4 bkColor = vec4(0.5, 0.5, 0.5, 1.0);
            vec2 tex = aCoordinate;

            vec2 upLeftUV = vec2(tex.x - 1.0 / TexSize.x, tex.y - 1.0 / TexSize.y);
            vec4 curColor = texture2D(vTexture, aCoordinate);
            vec4 upLeftColor = texture2D(vTexture, upLeftUV);
            vec4 delColor = curColor - upLeftColor;
            float h = 0.3 * delColor.x + 0.59 * delColor.y + 0.11 * delColor.z;
            gl_FragColor = vec4(h, h, h, 0.0) + bkColor;

        } else if (vChangeType == 8) {
            vec2 TexSize = vec2(400.0, 400.0);
            vec2 mosaicSize = vec2(8.0, 8.0);
            vec2 intXY = vec2(aCoordinate.x * TexSize.x, aCoordinate.y * TexSize.y);
            vec2 XYMosaic = vec2(floor(intXY.x / mosaicSize.x) * mosaicSize.x, floor(intXY.y / mosaicSize.y) * mosaicSize.y);
            vec2 UVMosaic = vec2(XYMosaic.x / TexSize.x, XYMosaic.y / TexSize.y);
            vec4 color = texture2D(vTexture, UVMosaic);
            gl_FragColor = color;

        } else {
            gl_FragColor = nColor;
        }

    }else{
        gl_FragColor = nColor;
    }
}