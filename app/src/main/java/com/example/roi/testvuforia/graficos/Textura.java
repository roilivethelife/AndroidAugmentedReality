package com.example.roi.testvuforia.graficos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.roi.testvuforia.AppInstance;

/**
 * Created by roi on 18/11/16.
 */

public class Textura {
    private int resourceId;
    private int textureHandle;

    private Textura(){
        this.resourceId =resourceId;
        textureHandle = 0;
    }

    public Textura(int resourceId){
        this();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        // Read in the resource
        Bitmap bitmap = BitmapFactory.decodeResource(AppInstance.getInstance().getContext().getResources(), resourceId, options);
        cargarTextura(bitmap);
        bitmap.recycle();
    }

    /**
     * Carga una textura para dibujar en openGL empleando el bitmap indicado
     * @param bitmap
     */
    public Textura(Bitmap bitmap){
        this();
        cargarTextura(bitmap);
    }


    private void cargarTextura(Bitmap bitmap){
        int[] textureHandleTemp = new int[1];
        Context context = AppInstance.getInstance().getContext();

        GLES20.glGenTextures(1, textureHandleTemp, 0);

        if (textureHandleTemp[0] != 0){
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandleTemp[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandleTemp[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }
        textureHandle=textureHandleTemp[0];
        Log.d("Textura","Textura cargada");
    }

    public int getTextureHandle() {
        return textureHandle;
    }

    public void deleteTexture(){
        int[] handles = new int[1];
        handles[0] = textureHandle;
        GLES20.glDeleteTextures(1,handles,0);
        this.textureHandle = 0;
    }

}
