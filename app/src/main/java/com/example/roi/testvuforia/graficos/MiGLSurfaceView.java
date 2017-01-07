package com.example.roi.testvuforia.graficos;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by roi on 5/11/16.
 */

public class MiGLSurfaceView extends GLSurfaceView {

    private float mPreviousX;
    private float mPreviousY;

    private OnTouchInterface onTouchInterface;

    public MiGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
    }


    public void setOnTouchInterface(OnTouchInterface onTouchInterface) {
        this.onTouchInterface = onTouchInterface;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (onTouchInterface!=null && onTouchInterface.onCustomTouchEvent(e)) {
            requestRender();
            return true;
        }

        return super.onTouchEvent(e);
    }

}
