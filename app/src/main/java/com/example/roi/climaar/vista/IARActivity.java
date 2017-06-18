package com.example.roi.climaar.vista;

import com.vuforia.State;

import java.util.ArrayList;

/**
 * 
 */
interface IARActivity {


    /**
     * Enviar estado de vuforia al presentador
     * @param state estado de vudoria
     * @return matriz modelviewMatrix
     */
    float[] updateVuforiaState(State state);


    void loadFiguras();

    void onSurfaceCreated();
    void onSurfaceChanged(int width, int height);

}