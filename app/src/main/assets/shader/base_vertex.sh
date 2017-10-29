attribute vec4 vPosition;
attribute vec2 vCoord;
uniform mat4 vMatrix;

varying vec2 textureCoordinate;
varying vec4 gPosition;

void main(){
    gl_Position = vMatrix * vPosition;
    textureCoordinate = vCoord;
    gPosition = vMatrix * vPosition;
}