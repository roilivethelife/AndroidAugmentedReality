package com.example.roi.testvuforia.graficos.Texto;

import android.opengl.GLES20;
import android.util.Log;

import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.Textura;
import com.example.roi.testvuforia.graficos.figuras.Figura;
import com.example.roi.testvuforia.graficos.figuras.VboClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by roi on 30/04/17.
 */

public class GLText extends VboClient {
    private int numChars;
    private int maxLength;

    public GLText(int maxLength) {
        super(FigureType.TEXT);
        this.maxLength = maxLength;
        ByteBuffer bb = ByteBuffer.allocateDirect(maxLength*6*8*4);//numChars*6vertexperChar*8floatperVertex*4bytesperfloat
        bb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
        vertexBuffer = bb.asFloatBuffer();
        numChars = 0;
        numVertices = 0;
        color[0]=1f;
        color[1]=0f;
        color[2]=0f;
        color[3]=1f;

    }

    @Override
    public void dibujar(Shader shader, float[] modelViewMatrix) {
        GLES20.glEnable( GLES20.GL_BLEND );
        GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
        super.dibujar(shader, modelViewMatrix);
        GLES20.glDisable( GLES20.GL_BLEND );
    }

    @Override
    public void loadFigura() {
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void editText(float[] vertexData, int numChars, Textura textura){
        if(numChars>maxLength){
            Log.e("GLText", "Cadena de mayor tamaño");
        }
        this.textura = textura;
        vertexBuffer.clear();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
        this.numChars = numChars;
        numVertices = numChars*6;
    }
}
