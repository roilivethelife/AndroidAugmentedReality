package com.example.roi.climaar.presentador;

import com.vuforia.State;

import java.util.*;

/**
 * 
 */
public interface IPresentador {

    /**
     * 
     */
    void btnMostrarElementosPushed();

    /**
     * 
     */
    void btnOcultarElementosPushed();

    /**
     * @param elemento elemento
     * @param visible visibilidad
     */
    void btnSetVisibleElemento(int elemento, boolean visible);

    /**
     * Actualiza el estado de vuforia para calcular la posicion correcta
     * Devuelve la matriz con la posicion lista para dibujar
     *
     * @param state estado de vudoria
     * @return matrix
     */
    float[] updateVuforiaState(State state);


    /**
     * Metodo llamado cuando ha habido un toque en pantalla
     * @param x posicion toque eje X
     * @param y posicion toque eje Y
     */
    void touchEvent(float x, float y);


    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();
    void loadFiguras();

}
