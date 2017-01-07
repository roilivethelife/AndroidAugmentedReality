package com.example.roi.testvuforia.graficos.figuras;

import android.opengl.GLES20;
import android.util.Log;

import com.example.roi.testvuforia.graficos.MiGLRender;
import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.Textura;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by roi on 20/11/16.
 */

public class Obj extends Figura{

    private  FloatBuffer vertexBuffer;
    private int numVertices;
    private float[] color;
    private Textura textura;

    private int modoDibujado = GLES20.GL_TRIANGLES;


    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Obj(Textura textura, ArrayList<Float> aVertexCoords, ArrayList<Float> aVertexNormal, ArrayList<Float> aVertexTexture, ArrayList<Integer> aFaces, float[] color) {
        this.textura = textura;
        numVertices=0;
        numVertices = aFaces.size()/3;//3Elementos por cara
        /*
        Por cada cara 3 vertices:
            Cada vertice:
                3Coordenadas 0 1 2
                3Normales    3 4 5
                2Textura     6 7
                Total:8floats
        3*8floats=24floats por cara.
         */
        float[] fVertexData = new float[8*numVertices];

        //Recorremos las caras y vamos completando el buffer
        for (int i = 0,j=0; i < aFaces.size(); i+=3,j++) {
            //hay que restar -1 a los indices para que empiecen en 0
            int iVerticeCoord= aFaces.get(i)-1;
            int iVerticeTex= aFaces.get(i+1);
            int iVerticeNorm= aFaces.get(i+2);
            if(iVerticeTex!=Integer.MIN_VALUE) iVerticeTex--;
            if(iVerticeNorm!=Integer.MIN_VALUE) iVerticeNorm--;
            //añadimos vertices coord
            fVertexData[j*8]=aVertexCoords.get(iVerticeCoord*3);
            fVertexData[j*8+1]=aVertexCoords.get(iVerticeCoord*3+1);
            fVertexData[j*8+2]=aVertexCoords.get(iVerticeCoord*3+2);
            //añadimos verticesNormales
            if(iVerticeNorm!=Integer.MIN_VALUE){
                fVertexData[j*8+3]=aVertexNormal.get(iVerticeNorm*3);
                fVertexData[j*8+4]=aVertexNormal.get(iVerticeNorm*3+1);
                fVertexData[j*8+5]=aVertexNormal.get(iVerticeNorm*3+2);
            }else{
                fVertexData[j*8+3]=0.0f;
                fVertexData[j*8+4]=1.0f;
                fVertexData[j*8+5]=0.0f;
                Log.d("OBJ","Error cargando obj, no hay normal definida");
            }

            if(iVerticeTex!=Integer.MIN_VALUE){
                fVertexData[j*8+6]=aVertexTexture.get(iVerticeTex*2);
                fVertexData[j*8+7]=1.0f-aVertexTexture.get(iVerticeTex*2+1);
            }else{
                fVertexData[j*8+6]=0.0f;
                fVertexData[j*8+7]=0.0f;
            }
        }

        //Creamos buffer de vertices
        ByteBuffer bb = ByteBuffer.allocateDirect(fVertexData.length * 4);//4bytes*float
        bb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        vertexBuffer = bb.asFloatBuffer();//creamos buffer float
        vertexBuffer.put(fVertexData);
        vertexBuffer.position(0);//reseteamos posicion

        this.color=color;
    }




    //Projection * View * Model


    /***
     * Funcion dibujar
     */
    @Override
    public void dibujar(Shader shader) {
        MiGLRender.checkGLError("pre");
        //Configuramos vertices
        vertexBuffer.position(0);//posicion inicial del primer vertice
        GLES20.glEnableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glVertexAttribPointer(shader.getmCoordHandle(), 3, GLES20.GL_FLOAT, false, 32, vertexBuffer);
        MiGLRender.checkGLError("1");

        //TODO: configurar normal( no necesario sin luces)

        //Configuramos textura
        vertexBuffer.position(6);//posicoin inicial del indice de textura
        GLES20.glEnableVertexAttribArray(shader.getmTexCoordinateHandle());
        GLES20.glVertexAttribPointer(shader.getmTexCoordinateHandle(), 2, GLES20.GL_FLOAT, false, 32, vertexBuffer);


        //Configuramos color
        GLES20.glUniform4fv(shader.getmColorHandle(),1,color,0);
        MiGLRender.checkGLError("3");

        //Activamos textura
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura.getTextureHandle());
        GLES20.glUniform1i(shader.getmTexUniformHandle(), 0);
        MiGLRender.checkGLError("4");
        // Draw the triangle
        GLES20.glDrawArrays(modoDibujado,0,numVertices);
        MiGLRender.checkGLError("5");

        GLES20.glDisableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glDisableVertexAttribArray(shader.getmTexCoordinateHandle());
        MiGLRender.checkGLError("6");
    }

    public void setModoDibujado(int modoDibujado) {
        this.modoDibujado = modoDibujado;
    }
}
