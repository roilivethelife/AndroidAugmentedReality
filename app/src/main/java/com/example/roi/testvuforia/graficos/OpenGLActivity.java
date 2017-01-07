package com.example.roi.testvuforia.graficos;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLActivity extends Activity {

    private MiGLSurfaceView mGLView;
    private MiGLRender mGLRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Crear instancia GLsurface
        mGLView = new MiGLSurfaceView(this);
        mGLRender = new MiGLRender(this);
        mGLView.setRenderer(mGLRender);
        mGLView.setOnTouchInterface(mGLRender);
        setContentView(mGLView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        mGLRender.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        mGLRender.onStop();
    }
}
