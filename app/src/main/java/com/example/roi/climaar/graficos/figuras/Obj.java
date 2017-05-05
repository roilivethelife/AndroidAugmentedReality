package com.example.roi.climaar.graficos.figuras;

import android.opengl.GLES20;
import android.util.Log;

import com.example.roi.climaar.graficos.figuras.ObjLoader.ObjReader;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by roi on 20/11/16.
 */

public class Obj extends Ibo implements Serializable{

    //CABECERA: contiene informacion necesaria para cargar el objeto
    private int resourceID;


    public Obj(int resourceID){
        super(FigureType.OBJ);
        this.resourceID =resourceID;
    }

    @Override
    public void loadFigura() {
        try {
            ObjReader objReader = new ObjReader(resourceID);
            color = objReader.getColor();
            textura = objReader.getTextura();
            FloatBuffer vertexBuffer = objReader.getVertexBuffer();
            ShortBuffer indexBuffer = objReader.getIndexBuffer();
            vboBuffer = new int[1];
            iboBuffer = new int[1];
            GLES20.glGenBuffers(1, vboBuffer, 0);
            GLES20.glGenBuffers(1, iboBuffer, 0);
            if (vboBuffer[0] > 0 && iboBuffer[0] > 0) {
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboBuffer[0]);
                GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT,
                        vertexBuffer, GLES20.GL_STATIC_DRAW);

                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboBuffer[0]);
                GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity()
                        * BYTES_PER_SHORT, indexBuffer, GLES20.GL_STATIC_DRAW);

                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
                GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
                indexCount = indexBuffer.capacity();
                isLoaded = true;
                Log.d("Obj","load figura ok!");
            } else {
                Log.e("Obj","load figura genBuffers error");
                isLoaded = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
