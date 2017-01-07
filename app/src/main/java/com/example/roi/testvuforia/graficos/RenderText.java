package com.example.roi.testvuforia.graficos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by roi on 15/11/16.
 */

public class RenderText {
    private Bitmap bitmap;
    private Canvas canvas;


    public RenderText(Context context){
        // Create an empty, mutable bitmap
        bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
        // get a canvas to paint over the bitmap
        canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);

        // get a background image from resources
        // note the image format must match the bitmap format
        //Drawable background = context.getResources().getDrawable(R.drawable.background);
        Drawable background = new BitmapDrawable(context.getResources(),bitmap);
        background.setBounds(0, 0, 256, 256);
        background.draw(canvas); // draw the background to our bitmap


    }


    void render() {

        int[] textures = new int[1];

        // Draw the text
        Paint textPaint = new Paint();
        textPaint.setTextSize(32);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
        // draw the text centered
        canvas.drawText("Hello World", 16, 112, textPaint);

        //Generate one texture pointer...
        GLES20.glGenTextures(1, textures, 0);
        //...and bind it to our array
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        //Create Nearest Filtered Texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        //Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        //Use the Android GLES20 to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        //Clean up
        bitmap.recycle();
    }
}

