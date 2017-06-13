package com.example.roi.climaar.presentador;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.mapa.Mapa;
import com.example.roi.climaar.presentador.vuforia.VuforiaControler;
import com.example.roi.climaar.vista.ARActivity;
import com.example.roi.climaar.vista.ARRender;
import com.example.roi.climaar.vista.IVista;
import com.example.roi.climaar.vista.MiGlSurfaceView;
import com.vuforia.State;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by roi on 11/06/17.
 */

public class Presentador implements  IPresentador{
    private static final String LOGTAG = "Presentador";


    private ARActivity arActivity;
    private MiGlSurfaceView glView;

    private Modelo modelo;
    private VuforiaControler vuforiaControler;
    private PositionControler positionControler;
    private IVista iVista;

    private Mapa actualMapa;

    public Presentador(IVista iVista, ARActivity activity, Mapa mapa) {
        this.iVista = iVista;
        this.arActivity = activity;
        this.actualMapa = mapa;
        this.vuforiaControler = new VuforiaControler(activity,this);

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
        if (glView != null)
        {
            glView.setVisibility(View.INVISIBLE);
            glView.onPause();
        }
    }

    @Override
    public void onResume() {
        Log.d(LOGTAG,"Presentador onResume");
        positionControler.onResume();
        // Resume the GL view:
        if (glView != null){
            glView.setVisibility(View.VISIBLE);
            glView.onResume();
        }
        vuforiaControler.onResume();
    }

    @Override
    public void onDestroy() {
        vuforiaControler.onDestroy();
    }

    @Override
    public void loadFiguras() {
        actualMapa.loadFiguras(modelo.getContext());
    }

    @Override
    public void onSurfaceCreated() {
        vuforiaControler.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        vuforiaControler.onSurfaceChanged(width,height);
    }

    public void initApplicationAR(){
        //CrearRender y SurfaceView
        ARRender arRender = new ARRender(arActivity,arActivity);
        glView = new MiGlSurfaceView(arActivity);
        glView.setOnTouchInterface(arActivity);
        glView.setRenderer(arRender);

        //Añadir glView
        arActivity.setContentView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        glView.setVisibility(View.VISIBLE);
        arRender.setActive(true);
    }

    public void vuforiaLoaded(){
        iVista.mostrarBarraCargando(false);
    }


}
