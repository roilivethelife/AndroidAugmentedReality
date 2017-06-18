package com.example.roi.climaar.presentador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.mapa.MapElement;
import com.example.roi.climaar.modelo.mapa.Mapa;
import com.example.roi.climaar.presentador.vuforia.VuforiaControler;
import com.example.roi.climaar.vista.ARActivity;
import com.example.roi.climaar.vista.ARRender;
import com.example.roi.climaar.vista.IVista;
import com.example.roi.climaar.vista.MenuOpciones;
import com.example.roi.climaar.vista.MiGlSurfaceView;
import com.vuforia.State;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by roi on 11/06/17.
 */

public class Presentador implements  IPresentador, PositionControlerCallback, MenuOpciones.MenuOpcionesListener, View.OnClickListener {
    private static final String LOGTAG = "Presentador";


    private ARActivity arActivity;
    private MiGlSurfaceView glView;

    private Modelo modelo;
    private VuforiaControler vuforiaControler;
    private PositionControler positionControler;
    private IVista iVista;

    private MenuOpciones menuOpciones;
    private boolean fabPressed;
    private FloatingActionButton fab;

    private Mapa actualMapa;

    public Presentador(IVista iVista, ARActivity activity, Mapa mapa) {
        this.iVista = iVista;
        this.arActivity = activity;
        this.actualMapa = mapa;
        this.vuforiaControler = new VuforiaControler(activity,this);

        this.modelo = Modelo.getInstance();
        this.positionControler = new PositionControler(activity,this, false);
    }


    /**
     */
    @Override
    public void btnFapPushed() {

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

        crearMenuOpciones();
        configurarFAB();


    }

    private void configurarFAB() {
        fab = iVista.getFAB();
        fab.setOnClickListener(this);
    }

    private void crearMenuOpciones() {
        menuOpciones = iVista.getMenuOpciones();
        for (MapElement mapElement :
                actualMapa.mapaElements) {
            menuOpciones.addElemento(arActivity,mapElement,"estado",true);
        }
        menuOpciones.makeMenu(arActivity,actualMapa.getNombre(),true,true);
        menuOpciones.setListener(this);
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
        fabPressed = false;
        menuOpciones.ocultarMenu();
    }

    @Override
    public void onDestroy() {
        vuforiaControler.onDestroy();
    }

    @Override
    public void onBackPressed() {
        askUserExit();
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
        ARRender arRender = new ARRender(arActivity,arActivity, actualMapa);
        glView = new MiGlSurfaceView(arActivity);
        glView.setOnTouchInterface(arActivity);
        glView.setRenderer(arRender);

        //Añadir glView
        iVista.addGLView(glView);
        glView.setVisibility(View.VISIBLE);
        arRender.setActive(true);
    }

    public void vuforiaLoaded(){
        iVista.mostrarBarraCargando(false);
    }


    /**
     * LLamado cuando hay un cambio en el tipo de tracking realizado
     * @param status estado TrackStatus
     */
    @Override
    public void onStateChanged(PositionControler.TrackStatus status) {
        switch (status){
            case NOT_TRACKED:
                iVista.mostrarTextoDebug("Not tracked");
                break;
            case TRACKED:
                iVista.mostrarTextoDebug("Tracked");
                break;
            case EXT_TRACKED:
                iVista.mostrarTextoDebug("Ext tracked");
                break;
            case GIRO_TRACKED:
                iVista.mostrarTextoDebug("Giro tracked");
                break;

        }
    }

    @Override
    public void barometroCalibrado() {
        iVista.mostrarToast("Barometro Calibrado");
    }

    @Override
    public void giroCalibrado() {
        iVista.mostrarToast("Giro Calibrado");

    }

    @Override
    public void setUseBarometerPressed(boolean useBarometer) {
        positionControler.setUseBarometer(useBarometer);
    }

    @Override
    public void setExtTrackingPressed(boolean extTrackinActive) {
        vuforiaControler.setExtendedTrackingActive(extTrackinActive);
    }

    @Override
    public void setVisibleElementPressed(MapElement element, boolean isVisible) {
        element.visible = isVisible;
    }

    @Override
    public void resetTrackingPressed() {
        positionControler.resetVars();
    }

    @Override
    public void exitButtonPressed() {
        fabMenuClose();
        askUserExit();
    }

    private void askUserExit(){
        new AlertDialog.Builder(arActivity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Saliendo")
                .setMessage("¿Está seguro de que quiere salir?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arActivity.finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab){
            fabPressed = !fabPressed;
            if(fabPressed){
                fabMenuOpen();
            }else{
                fabMenuClose();
            }
        }
    }

    private void fabMenuOpen() {
        fab.setImageResource(R.drawable.ic_cancel_black_24dp);
        menuOpciones.mostrarMenu();
    }

    private void fabMenuClose(){
        fab.setImageResource(R.drawable.ic_menu_black_24dp);
        menuOpciones.ocultarMenu();
    }
}
