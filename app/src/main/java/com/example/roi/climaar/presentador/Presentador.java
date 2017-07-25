package com.example.roi.climaar.presentador;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.JsonRest.WebRestData;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.despacho.Despacho;
import com.example.roi.climaar.modelo.despacho.DespachoElement;
import com.example.roi.climaar.modelo.figuras.Texto.GLTextDrawer;
import com.example.roi.climaar.presentador.vuforia.VuforiaControler;
import com.example.roi.climaar.vista.ARActivity;
import com.example.roi.climaar.vista.ARRender;
import com.example.roi.climaar.vista.IVista;
import com.example.roi.climaar.vista.MenuOpciones;
import com.example.roi.climaar.vista.MiGlSurfaceView;
import com.vuforia.State;


/**
 * Created by roi on 11/06/17.
 */

public class Presentador implements  IPresentador, PositionControlerCallback, MenuOpciones.MenuOpcionesListener, View.OnClickListener {
    private static final String LOGTAG = "Presentador";

    private static final boolean USE_BAROMETER_DEF  = false;
    private static final boolean USE_EXT_TRACK_DEF  = true;

    private ARActivity arActivity;
    private GLSurfaceView glView;

    private Modelo modelo;
    private VuforiaControler vuforiaControler;
    private PositionControler positionControler;
    private IVista iVista;

    private WebRestData webRestData;

    private MenuOpciones menuOpciones;
    private boolean fabPressed;
    private FloatingActionButton fab;

    private Despacho actualDespacho;

    public Presentador(IVista iVista, ARActivity activity, Despacho despacho) {
        this.iVista = iVista;
        this.arActivity = activity;
        this.actualDespacho = despacho;
        this.vuforiaControler = new VuforiaControler(activity,this,USE_EXT_TRACK_DEF);

        this.modelo = Modelo.getInstance();
        this.positionControler = new PositionControler(activity,this, USE_BAROMETER_DEF);

        this.webRestData = new WebRestData(Integer.toString(despacho.getNumDespacho()), despacho.despachoElements);
    }


    /**
     */
    @Override
    public void btnFapPushed() {

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

    @Override
    public void onCreate() {
        Log.d(LOGTAG,"Presentador onCreate");
        iVista.mostrarBarraCargando(true);
        vuforiaControler.onCreate();

        crearMenuOpciones(positionControler.hasBarometer(),USE_BAROMETER_DEF,USE_EXT_TRACK_DEF);
        configurarFAB();
    }

    private void configurarFAB() {
        fab = iVista.getFAB();
        fab.setOnClickListener(this);
    }

    /**
     * Crear menu opciones
     */
    private void crearMenuOpciones(boolean hasBarometer,boolean useBaro, boolean extendedTrackEnabled) {
        menuOpciones = iVista.getMenuOpciones();
        for (DespachoElement despachoElement :
                actualDespacho.despachoElements) {
            menuOpciones.addElemento(arActivity, despachoElement,"estado",true);
        }
        menuOpciones.makeMenu(arActivity, actualDespacho.getNombre(),hasBarometer,useBaro,extendedTrackEnabled);
        menuOpciones.setListener(this);
    }

    @Override
    public void onPause() {
        Log.d(LOGTAG,"Presentador onPause");
        vuforiaControler.onPause();
        positionControler.onPause();
        webRestData.onPause();
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
        webRestData.onResume();
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
        GLTextDrawer.reloadTextDrawer();
        actualDespacho.loadFiguras(modelo.getContext());
    }

    @Override
    public void onSurfaceCreated() {
        //Cargar
        vuforiaControler.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        vuforiaControler.onSurfaceChanged(width,height);
    }

    public void initApplicationAR(){
        //CrearRender y SurfaceView
        ARRender arRender = new ARRender(arActivity,arActivity, actualDespacho);
        glView = arRender;
        arRender.setRenderer(arRender);

        //Añadir glView
        iVista.addGLView(arRender);
        arRender.setVisibility(View.VISIBLE);
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
                iVista.mostrarTextoDebug("No inicializado: acerque el dispositivo al marcador");
                break;
            case DETECTED:
                iVista.mostrarTextoDebug("No inicializado: marcador detectado\n" +
                        "Acerque más el disposivo, de frente al marcador");
            case TRACKED:
                iVista.mostrarToast("Sistema calibrado, ya puede mover el dispositivo libremente");
                iVista.mostrarTextoDebug("Iniciado: precisión buena");
                break;
            case EXT_TRACKED:
                iVista.mostrarTextoDebug("Iniciado: precisión media");
                break;
            case GIRO_TRACKED:
                iVista.mostrarTextoDebug("Iniciado: precisión baja. Solo se detectan cambios de orientación");
                break;

        }
    }

    @Override
    public void barometroCalibrado() {
        iVista.mostrarToast("Barometro Calibrado");
    }

    @Override
    public void giroCalibrado() {
        //iVista.mostrarToast("Giro Calibrado");
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
    public void setVisibleElementPressed(DespachoElement element, boolean isVisible) {
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
                .setTitle("Salir modo AR")
                .setMessage("¿Está seguro de que quiere salir del modo Realidad Aumentada y volver al menú principal?")
                .setPositiveButton("Salir", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arActivity.finish();
                    }

                })
                .setNegativeButton("Cancelar", null)
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
