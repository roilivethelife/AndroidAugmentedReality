package com.example.roi.climaar.presentador;

import android.app.Activity;
import android.util.Log;

import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.mapa.Mapa;
import com.example.roi.climaar.vista.IVista;
import com.vuforia.State;


/**
 * Created by roi on 11/06/17.
 */

public class Presentador implements  IPresentador{
    private static final String LOGTAG = "Presentador";


    private Modelo modelo;
    private VuforiaControler vuforiaControler;
    private PositionControler positionControler;
    private IVista iVista;

    private Mapa actualMapa;

    public Presentador(IVista iVista, Activity activity, Mapa mapa) {
        this.iVista = iVista;
        this.vuforiaControler = new VuforiaControler(activity);
        this.actualMapa = mapa;

        this.modelo = Modelo.getInstance();
        this.positionControler = new PositionControler(activity);
    }


    /**
     *
     */
    @Override
    public void btnMostrarElementosPushed() {

    }

    /**
     *
     */
    @Override
    public void btnOcultarElementosPushed() {

    }

    /**
     * @param elemento elemento
     * @param visible  visibilidad
     */
    @Override
    public void btnSetVisibleElemento(int elemento, boolean visible) {

    }

    /**
     * Actualiza el estado de vuforia para calcular la posicion correcta
     * Ha de ser llamado en cada frame antes de obtener la matriz
     * mediante getModelViewMatrix()
     *
     * @param state estado de vudoria
     */
    @Override
    public float[] updateVuforiaState(State state) {
        return positionControler.updateLocation(state);
    }


    /**
     * Metodo llamado cuando ha habido un toque en pantalla
     *
     * @param x posicion toque eje X
     * @param y posicion toque eje Y
     */
    @Override
    public void touchEvent(float x, float y) {

    }

    @Override
    public void onCreate() {
        Log.d(LOGTAG,"Presentador onCreate");
        iVista.mostrarBarraCargando(true);
        vuforiaControler.onCreate();
    }

    @Override
    public void onPause() {
        Log.d(LOGTAG,"Presentador onPause");
        vuforiaControler.onPause();
        positionControler.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOGTAG,"Presentador onResume");
        positionControler.onResume();
        vuforiaControler.onResume();
        iVista.mostrarBarraCargando(false);
    }

    @Override
    public void onDestroy() {
        vuforiaControler.onDestroy();
    }

    @Override
    public void loadFiguras() {
        actualMapa.loadFiguras(modelo.getContext());
    }
}
