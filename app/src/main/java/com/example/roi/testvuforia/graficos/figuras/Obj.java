package com.example.roi.testvuforia.graficos.figuras;

import android.opengl.GLES20;
import android.util.Log;

import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.Textura;
import com.example.roi.testvuforia.graficos.figuras.ObjLoader.ObjReader;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by roi on 20/11/16.
 */

public class Obj extends Figura implements Serializable{

    //CABECERA: contiene informacion necesaria para cargar el objeto
    private int resourceID;


    //Propiedades obj
    protected transient FloatBuffer vertexBuffer;
    protected transient int numVertices;
    private transient float[] color;
    protected transient Textura textura;


    protected int modoDibujado = GLES20.GL_TRIANGLES;



    public Obj(int resourceID){
        super(FigureType.OBJ);
        this.resourceID =resourceID;
        try {
            ObjReader objReader = new ObjReader(resourceID);
            vertexBuffer = objReader.getVertexBuffer();
            numVertices = objReader.getNumVertices();
            color = objReader.getColor();
            textura = objReader.getTextura();
            isLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Obj(Textura textura, FloatBuffer vertexBuffer, int numVertices, float[] color, int resourceID ) {
        super(FigureType.OBJ);
        this.textura = textura;
        this.vertexBuffer = vertexBuffer;
        this.numVertices= numVertices;
        this.color=color;

        this.resourceID = resourceID;

        isLoaded = true;
    }

    public void setProperties(Textura textura, FloatBuffer vertexBuffer, int numVertices, float[] color){
        this.textura = textura;
        this.vertexBuffer = vertexBuffer;
        this.numVertices= numVertices;
        this.color=color;
    }

    @Override
    public void loadFigura() {
        try {
            new ObjReader(resourceID).setObjetoProperties(this);
            isLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Funcion dibujar
     */
    @Override
    public void dibujar(Shader shader,float[] modelViewMatrix) {
        //Configuramos vertices
        vertexBuffer.position(0);//posicion inicial del primer vertice
        GLES20.glEnableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glVertexAttribPointer(shader.getmCoordHandle(), 3, GLES20.GL_FLOAT, false, 32, vertexBuffer);
        //TODO: configurar normal( no necesario sin luces)

        //Configuramos textura
        vertexBuffer.position(6);//posicoin inicial del indice de textura
        GLES20.glEnableVertexAttribArray(shader.getmTexCoordinateHandle());
        GLES20.glVertexAttribPointer(shader.getmTexCoordinateHandle(), 2, GLES20.GL_FLOAT, false, 32, vertexBuffer);


        //Configuramos color
        GLES20.glUniform4fv(shader.getmColorHandle(),1,color,0);

        //Activamos textura
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura.getTextureHandle());
        GLES20.glUniform1i(shader.getmTexUniformHandle(), 0);

        //Configuramos modelView
        GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,modelViewMatrix,0);

        // Draw the triangle
        GLES20.glDrawArrays(modoDibujado,0,numVertices);

        GLES20.glDisableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glDisableVertexAttribArray(shader.getmTexCoordinateHandle());
    }

    public void setModoDibujado(int modoDibujado) {
        this.modoDibujado = modoDibujado;
    }
}
