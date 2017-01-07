package com.example.roi.testvuforia.graficos.figuras;

import android.opengl.GLES20;

import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.Textura;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by roi on 6/11/16.
 */
@Deprecated
public class Triangulo extends Figura{
    private final FloatBuffer vertexBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f,   // top
            -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };
    private Textura textura;
    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Triangulo( Textura textura) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        this.textura = textura;
    }

    //Projection * View * Model


    /***
     * Funcion dibujar
     */
    @Override
    public void dibujar(Shader shader) {
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(shader.getmCoordHandle());

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(shader.getmCoordHandle(), COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);


        // Set color for drawing the triangle
        GLES20.glUniform4fv(shader.getmColorHandle(), 1, color, 0);


        //Activamos textura
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textura.getTextureHandle());
        GLES20.glUniform1i(shader.getmTexUniformHandle(),0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(shader.getmCoordHandle());
    }

}
