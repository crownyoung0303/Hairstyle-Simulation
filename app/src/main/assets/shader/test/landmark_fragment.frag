precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;

uniform float uLandmarkX[106];
uniform float uLandmarkY[106];

float alw = 0.005;

bool inRange( float c1, float c2 ) {
  return abs( c1 - c2 ) < alw;
}

bool isLandmark(vec2 point) {
  for (int i = 0; i < 106; i++) {
      float fX = 1.0 - uLandmarkX[i] / 480.0;
      float fY = uLandmarkY[i] / 640.0;
      float mX = point.x;
      float mY = point.y;
      if ( inRange(mX, fX) && inRange(mY, fY) ) {
        return true;
      }
  }
  return false;
}

vec4 getLandmark(vec4 camera) {
    vec2 uv = textureCoordinate;
    float radius = 1.0;
    vec3 green = vec3(0.0, 1.0, 0.0);

    for (int i = 0; i < 106; i++) {
        float fX = 1.0 - uLandmarkX[i] / 480.0;
        float fY = uLandmarkY[i] / 640.0;
        vec2 circle = vec2(fX, fY);

        if(distance(uv, circle) <= radius) {
            camera = vec4(green, 1.0);
        }
    }

    return camera;
}

vec4 circle(vec2 uv, vec2 pos, float rad, vec3 color) {
	float d = length(pos - uv) - rad;
	float t = clamp(d, 0.0, 1.0);
	return vec4(color, 1.0 - t);
}

vec4 circle2(vec2 pos, vec2 center, float radius, vec4 color, vec4 camera) {
    if (length(pos - center) < radius) {
        return color;
    } else {
        return camera;
    }
}

void main() {
    vec4 camera = texture2D(vTexture, textureCoordinate);

    // gl_FragColor = (isLandmark(textureCoordinate))
    //     ? vec4(0.0, 1.0, 0.0, 1.0)
    //     : camera;

    vec4 green = vec4(0.0, 1.0, 0.0, 1.0);

    float screenW = 720.0;
    float screenH = 1280.0;
    vec2 iResolution = vec2(screenW, screenH);

    vec2 uv = textureCoordinate * iResolution.xy;
    vec2 center = vec2(0.5, 0.5);
    float radius = 5.0;

    //gl_FragColor = circle2(uv, center * iResolution.xy, radius, green, camera);

    for (int i = 0; i < 106; i+=3) {
        float fX = uLandmarkX[i];
        float fY = uLandmarkY[i];
        vec2 center = vec2(fX, fY);

        vec4 point = circle2(uv, center * iResolution.xy, radius, green, camera);
        camera = mix(camera, point, point.a);
    }
    gl_FragColor = camera;
}