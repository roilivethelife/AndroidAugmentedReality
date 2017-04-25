package com.example.roi.testvuforia.graficos.figuras;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.roi.testvuforia.AppInstance;
import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.figuras.ObjLoader.ObjReader;
import com.example.roi.testvuforia.graficos.Shader;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by roi on 24/04/17.
 */

public class SueloRadiante extends Figura implements Serializable{


    private transient Obj pipe;

    private static final float[] COLOR_BLUE = {0.2f, 0.4f, 1f, 1f};
    private static final float[] COLOR_RED = {1f, 0.07f, 0f, 1f};
    boolean colorRed = false;

    private static final float ANCHO_PIPE = 5.0f;
    private static final float LARGO_PIPE = 5.0f;



    private float ancho;
    private float largo;

    private static int resourceID = R.raw.pipe;


    public SueloRadiante(int ancho, int largo) {
        super(FigureType.SUELO_RADIANTE);
        try {
            pipe = new ObjReader(resourceID).getObjeto();
            isLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.ancho = ancho;
        this.largo = largo;
    }

    @Override
    public void loadFigura() {
        try {
            pipe = new ObjReader(resourceID).getObjeto();
            isLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dibujar(Shader shader, float[] modelViewMatrix) {
        float[] modelViewTemp = new float[16];

        //Configuramos vertices
        pipe.vertexBuffer.position(0);//posicion inicial del primer vertice
        GLES20.glEnableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glVertexAttribPointer(shader.getmCoordHandle(), 3, GLES20.GL_FLOAT, false, 32, pipe.vertexBuffer);

        //Configuramos textura
        pipe.vertexBuffer.position(6);//posicoin inicial del indice de textura
        GLES20.glEnableVertexAttribArray(shader.getmTexCoordinateHandle());
        GLES20.glVertexAttribPointer(shader.getmTexCoordinateHandle(), 2, GLES20.GL_FLOAT, false, 32, pipe.vertexBuffer);


        //Configuramos color
        if(colorRed){
            GLES20.glUniform4fv(shader.getmColorHandle(),1,COLOR_RED,0);
        }else{
            GLES20.glUniform4fv(shader.getmColorHandle(),1,COLOR_BLUE,0);
        }
        //Activamos textura
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, pipe.textura.getTextureHandle());
        GLES20.glUniform1i(shader.getmTexUniformHandle(), 0);


        /**
         * 1: dibujar I
         *
         *
         */
        //50cm de separacion entre tuberias
        float separacionTuberias = 30f;
        /*Matrix.translateM(modelViewTemp,0,modelViewMatrix,0,0,0,0);
        Matrix.rotateM(modelViewTemp,0,90,0,1,0);
        Matrix.scaleM(modelViewTemp,0,0.5f,0.5f,largo/LARGO_PIPE);
        GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,modelViewTemp,0);
        GLES20.glDrawArrays(pipe.modoDibujado,0,pipe.numVertices);
        */
        for (int x = 0, j=0; x < (ancho); x+=(int)separacionTuberias, j++) {
            Matrix.translateM(modelViewTemp,0,modelViewMatrix,0,x,0,0);
            Matrix.scaleM(modelViewTemp,0,0.5f,0.5f,largo/LARGO_PIPE);
            GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,modelViewTemp,0);
            GLES20.glDrawArrays(pipe.modoDibujado,0,pipe.numVertices);

            //Dibujamos lateral
            if(j%2==0){//si es par dibujamos lateral en 0
                Matrix.translateM(modelViewTemp,0,modelViewMatrix,0,x,0,0);
            }else {//si es impar en largo
                Matrix.translateM(modelViewTemp,0,modelViewMatrix,0,x,0,largo);
            }
            Matrix.rotateM(modelViewTemp,0,90,0,1,0);
            Matrix.scaleM(modelViewTemp,0,0.5f,0.5f,separacionTuberias/LARGO_PIPE);
            GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,modelViewTemp,0);
            GLES20.glDrawArrays(pipe.modoDibujado,0,pipe.numVertices);
        }

        GLES20.glDisableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glDisableVertexAttribArray(shader.getmTexCoordinateHandle());
    }
}
