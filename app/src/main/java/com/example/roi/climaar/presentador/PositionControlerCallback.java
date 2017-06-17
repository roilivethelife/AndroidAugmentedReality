package com.example.roi.climaar.presentador;


import com.example.roi.climaar.presentador.PositionControler.TrackStatus;
/**
 * Created by roi on 17/06/17.
 */

public interface PositionControlerCallback {

    /**
     * LLamado cuando hay un cambio en el tipo de tracking realizado
     */
    void onStateChanged(TrackStatus status);

    void barometroCalibrado();
    void giroCalibrado();

}
