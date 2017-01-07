package com.example.roi.testvuforia.graficos.figuras;

import android.opengl.GLES20;

import com.example.roi.testvuforia.graficos.MiGLRender;
import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.Textura;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by roi on 15/11/16.
 */

@Deprecated
public class Plano extends Figura{


    private final FloatBuffer vertexBuffer;
    private final ShortBuffer indexBuffer;
    private Textura textura;

    static float vertexCoords[] = {
            -1.0f, -1.0f, 0.0f,//abajo izq
            0.0f, 1.0f,//X,Y textura
            1.0f, -1.0f, 0.0f,//abajo der
            1.0f,1.0f,//X,Y textura
            1.0f, 1.0f, 0.0f,//arriba der
            1.0f,0.0f,//X,Y textura
            -1.0f, 1.0f, 0.0f,//arriba izq
            0.0f,0.0f,//X,Y textura
    };

    private static float[] color = {1.0f, 0.0f, 1.0f, 1.0f};

    static short vertexIndex[] = {
            0,1,3,
            3,1,2
    };



    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Plano(Shader shader, Textura textura) {

        //Creamos buffer de vertices
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexCoords.length * 4);//4bytes*float
        bb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        vertexBuffer = bb.asFloatBuffer();//creamos buffer float
        vertexBuffer.put(vertexCoords);//añadimos los vertices
        vertexBuffer.position(0);//reseteamos posicion

        //Creamos buffer de indices
        bb = ByteBuffer.allocateDirect(vertexIndex.length*2);//2bytes*short
        bb.order(ByteOrder.nativeOrder());
        indexBuffer = bb.asShortBuffer();
        indexBuffer.put(vertexIndex);
        indexBuffer.position(0);

        this.textura=textura;
    }

    //Projection * View * Model


    /***
     * Funcion dibujar
     */
    @Override
    public void dibujar(Shader shader) {
        MiGLRender.checkGLError("pre");
        vertexBuffer.position(0);//posicion inicial del primer vertice
        GLES20.glEnableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glVertexAttribPointer(shader.getmCoordHandle(),3,GLES20.GL_FLOAT,false,20,vertexBuffer);
        MiGLRender.checkGLError("1");


        vertexBuffer.position(3);//posicoin inicial del indice de textura
        GLES20.glEnableVertexAttribArray(shader.getmTexCoordinateHandle());
        GLES20.glVertexAttribPointer(shader.getmTexCoordinateHandle(),2,GLES20.GL_FLOAT,false,20,vertexBuffer);
        MiGLRender.checkGLError("3");

        //color
        GLES20.glUniform4fv(shader.getmColorHandle(),1,color,0);

        //Activamos textura
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textura.getTextureHandle());
        GLES20.glUniform1i(shader.getmTexUniformHandle(),0);
        MiGLRender.checkGLError("4");
        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,vertexIndex.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);
        MiGLRender.checkGLError("5");

        GLES20.glDisableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glDisableVertexAttribArray(shader.getmTexCoordinateHandle());
        MiGLRender.checkGLError("6");
    }
}
