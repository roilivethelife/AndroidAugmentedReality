precision mediump float;

uniform sampler2D u_Texture; //info textura
varying vec2 v_TexCoordinate;


uniform vec4 uColor;
void main() {
    gl_FragColor = uColor*texture2D(u_Texture, v_TexCoordinate);
//  gl_FragColor = vColor;
}