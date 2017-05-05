package com.example.roi.climaar.graficos.figuras;

import android.opengl.GLES20;
import android.util.Log;

import com.example.roi.climaar.graficos.Shader;
import com.example.roi.climaar.graficos.Textura;

/**
 * Created by roi on 1/05/17.
 */

public abstract class Ibo extends Figura {
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

    protected transient int[] vboBuffer;
    protected transient int[] iboBuffer;
    protected transient int indexCount;
    protected Textura textura;
    protected float[] color = {1f, 1f, 1f, 1f};



    public Ibo(Figura.FigureType type){
        super(type);
    }

    @Override
    public abstract void loadFigura();

    public void dibujar(Shader shader, float[] modelViewMatrix){
        if (vboBuffer[0] > 0 && iboBuffer[0] > 0) {
            // Pass in the position information
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboBuffer[0]);
            GLES20.glEnableVertexAttribArray(shader.getmCoordHandle());
            GLES20.glVertexAttribPointer(shader.getmCoordHandle(), POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, STRIDE, 0);

            //añadir normal.... aun no hay luces :)

            // Pass in the texture information
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboBuffer[0]);
            GLES20.glEnableVertexAttribArray(shader.getmTexCoordinateHandle());
            GLES20.glVertexAttribPointer(shader.getmTexCoordinateHandle(), TEXTURE_COORDINATE_DATA_SIZE, GLES20.GL_FLOAT, false,
                    STRIDE, (POSITION_DATA_SIZE + NORMAL_DATA_SIZE) * BYTES_PER_FLOAT);

            //Configuramos color
            GLES20.glUniform4fv(shader.getmColorHandle(), 1, color, 0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textura.getTextureHandle());
            GLES20.glUniform1i(shader.getmTexUniformHandle(), 0);

            // Draw the figura
            GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(),1,false,modelViewMatrix,0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboBuffer[0]);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);

            // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }else {
            Log.e("IBO","No dibujado, Buffers = 0");
        }
    }
}
