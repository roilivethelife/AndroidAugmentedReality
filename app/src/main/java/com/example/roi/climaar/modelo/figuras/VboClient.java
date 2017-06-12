package com.example.roi.climaar.modelo.figuras;

import android.opengl.GLES20;

import com.example.roi.climaar.vista.Shader;

import java.nio.FloatBuffer;

/**
 * Created by roi on 1/05/17.
 */

public abstract class VboClient extends Figura {
    /** Size of the position data in elements. */
    static final int POSITION_DATA_SIZE = 3;
    /** Size of the normal data in elements. */
    static final int NORMAL_DATA_SIZE = 3;
    /** Size of the texture coordinate data in elements. */
    static final int TEXTURE_COORDINATE_DATA_SIZE = 2;
    /** How many bytes per float. */
    static final int BYTES_PER_FLOAT = 4;
    static final int BYTES_PER_SHORT = 2;
    static final int STRIDE = (POSITION_DATA_SIZE + NORMAL_DATA_SIZE + TEXTURE_COORDINATE_DATA_SIZE) * BYTES_PER_FLOAT;

    protected transient FloatBuffer vertexBuffer;
    protected transient int numVertices;
    protected Textura textura;
    protected float[] color = {1f, 1f, 1f, 1f};


    protected transient boolean isLoaded=false;


    public VboClient(FigureType type){
        super(type);

    }


    public void dibujar(Shader shader, float[] modelViewMatrix){
        //Configuramos vertices
        vertexBuffer.position(0);//posicion inicial del primer vertice
        GLES20.glEnableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glVertexAttribPointer(shader.getmCoordHandle(), POSITION_DATA_SIZE, GLES20.GL_FLOAT,
                false, STRIDE, vertexBuffer);

        //Configuramos textura
        vertexBuffer.position(6);//posicoin inicial del indice de textura
        GLES20.glEnableVertexAttribArray(shader.getmTexCoordinateHandle());
        GLES20.glVertexAttribPointer(shader.getmTexCoordinateHandle(), TEXTURE_COORDINATE_DATA_SIZE,
                GLES20.GL_FLOAT, false, STRIDE, vertexBuffer);

        //COLOR
        GLES20.glUniform4fv(shader.getmColorHandle(),1,color,0);

        //Activamos textura
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura.getTextureHandle());
        GLES20.glUniform1i(shader.getmTexUniformHandle(), 0);

        //Dibujar
        GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,modelViewMatrix,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,numVertices);

        //Clean
        GLES20.glDisableVertexAttribArray(shader.getmCoordHandle());
        GLES20.glDisableVertexAttribArray(shader.getmTexCoordinateHandle());
    }
}
