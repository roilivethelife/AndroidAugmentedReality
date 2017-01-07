uniform mat4 modelMatrix; //matriz del modelo
uniform mat4 uPVMatrix;  //matriz proyecion*view

attribute vec4 aCoordenate;  //posicion vertice

//Variables para las texturas
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;


void main() {
   v_TexCoordinate = a_TexCoordinate; //textura a fragment shader
    //gl_PointSize = 5.0;
//ORDEN multiplicaciones: projecion*view*model*v_position
   gl_Position = uPVMatrix* modelMatrix * aCoordenate;

}