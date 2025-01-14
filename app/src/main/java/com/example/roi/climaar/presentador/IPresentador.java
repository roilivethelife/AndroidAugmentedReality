package com.example.roi.climaar.presentador;

import com.example.roi.climaar.vista.MenuOpciones.MenuOpcionesListener;
import com.vuforia.State;

import java.util.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 
 */
public interface IPresentador {

    /**
     * 
     */
    void btnFapPushed();


    /**
     * Actualiza el estado de vuforia para calcular la posicion correcta
     * Devuelve la matriz con la posicion lista para dibujar
     *
     * @param state estado de vudoria
     * @return matrix
     */
    float[] updateVuforiaState(State state);



    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();
    void loadFiguras();


    void onSurfaceCreated();
    void onSurfaceChanged(int width, int height);


    void onBackPressed();
}
