package com.example.roi.climaar.vista;

import android.content.Context;
import android.view.MotionEvent;

/**
 * 
 */
@Deprecated
public class MiGlSurfaceView extends android.opengl.GLSurfaceView {

    /**
     * @param context Contexto del sistema
     */
    public MiGlSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
    }

}