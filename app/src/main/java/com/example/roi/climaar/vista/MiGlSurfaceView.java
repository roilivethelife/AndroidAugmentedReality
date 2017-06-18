package com.example.roi.climaar.vista;

import android.content.Context;
import android.view.MotionEvent;

/**
 * 
 */
public class MiGlSurfaceView extends android.opengl.GLSurfaceView {

    /**
     * 
     */
    private OnTouchInterface onTouchInterface;

    /**
     * @param context Contexto del sistema
     */
    public MiGlSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
    }

    /**
     * @param onTouchInterface Interfaz OnTouchInterface
     */
    public void setOnTouchInterface(OnTouchInterface onTouchInterface) {
        this.onTouchInterface = onTouchInterface;
    }

    /**
     * @param motionEvent motionEvent
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (onTouchInterface!=null && onTouchInterface.onCustomTouchEvent(motionEvent)) {
            requestRender();
            return true;
        }

        return super.onTouchEvent(motionEvent);
    }

}