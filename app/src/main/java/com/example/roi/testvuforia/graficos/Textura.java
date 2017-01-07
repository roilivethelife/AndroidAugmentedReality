package com.example.roi.testvuforia.graficos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by roi on 18/11/16.
 */

public class Textura {
    private int textureHandle;

    public Textura(Context context, int resourceId){
        int[] textureHandleTemp = new int[1];

        GLES20.glGenTextures(1, textureHandleTemp, 0);

        if (textureHandleTemp[0] != 0){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

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
    }

    public int getTextureHandle() {
        return textureHandle;
    }
}
