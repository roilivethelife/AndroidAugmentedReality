package com.example.roi.climaar.presentador.vuforia;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.roi.climaar.R;
import com.example.roi.climaar.presentador.Presentador;
import com.example.roi.climaar.vista.ARRender;
import com.example.roi.climaar.vista.MiGlSurfaceView;
import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Renderer;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vec2I;
import com.vuforia.VideoBackgroundConfig;
import com.vuforia.VideoMode;
import com.vuforia.Vuforia;

/**
 * Esta clase controla la activación y desactivación de la librería Vuforia
 * También se encarga de configurar la cámara.
 */

public class VuforiaControler implements SampleApplicationControl{
    private static final String LOGTAG = "VuforiaControler";
    private final static String licenseKey = "AWpyQvb/////AAAAGZx5ZAoRvk6msRcJbt6TCoh4ZH7rhIazxFYCsaoEBwNs9ZZxKeK5+QPR3HTN3Bq3wr1VwQWjMW+75kbrMEG4Jwzu7EAjn4vVw9MZZKuJMn0CThCtV6EeYwRXfsoB8E+vILrv6885BpcZ9ytCaOjZYsKBYS370c739mpOmCjMP8ksBarHN52ZwFjLYW/K6VOHCbSNrHzEOy0AAtPT3RDMbC1crImFUlCIIPCqalThOP9t1llOZlR5WUddD9Ee4A18pV+Pytz24Qtkkpz+eBrVmbA6yzhjDW+O5TpNJID9wUnuvjVvL9ysROBIwofPb6yyyNye/gHlkLSKvR9rabm2bzNf2xS5OPk2ua/3sdoOkDw1";

    private boolean extendedTrackingActive=true;
    private DataSet currentDataSet;

    private Activity activity;
    private Presentador presentador;

    SampleApplicationSession vuforiaAppSession;

    public  VuforiaControler(Activity activity, Presentador presentador){
        this.activity = activity;
        this.presentador = presentador;
    }

    /**
     * Inicia el tracker de vuforia
     */
    public void onCreate(){

        vuforiaAppSession = new SampleApplicationSession(this,licenseKey);
        vuforiaAppSession.initAR(activity);
    }

    public void onResume(){
        try{
            vuforiaAppSession.resumeAR();
        } catch (SampleApplicationException e){
            Log.e(LOGTAG, e.getString());
        }
    }

    public void onPause(){
        try{
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e){
            Log.e(LOGTAG, e.getString());
        }

    }

    public void onDestroy(){
        try{
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
    }

    /**
     * Iniciar el tracker
     * @return true si correcto
     */
    @Override
    public boolean doInitTrackers() {
        // Indicate if the trackers were initialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;

        // Trying to initialize the image tracker
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null)
        {
            Log.e(LOGTAG, "Tracker already initialized or camera already started");
            result = false;
        } else
        {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
        return result;
    }

    /**
     * Desiniciar el tracker
     * @return true si correcto
     */
    @Override
    public boolean doDeinitTrackers() {
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        return result;
    }

    @Override
    public boolean doLoadTrackersData() {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (currentDataSet == null)
            currentDataSet = objectTracker.createDataSet();

        if (currentDataSet == null)
            return false;

        //cargarNuevoMapa datos del tracker: leer archivo que contiene las imágenes a rastrear
        currentDataSet = objectTracker.createDataSet();
        if(!currentDataSet.load("Marker1.xml", STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;

        if(!objectTracker.activateDataSet(currentDataSet))
            return false;

        int numTrackables = currentDataSet.getNumTrackables();
        for (int i = 0; i < numTrackables; i++) {
            Trackable trackable = currentDataSet.getTrackable(i);
            if(extendedTrackingActive) {
                trackable.startExtendedTracking();
            }
            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data "
                    + trackable.getUserData());
        }

        return true;
    }

    @Override
    public boolean doUnloadTrackersData() {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (currentDataSet != null && currentDataSet.isActive())
        {
            if (objectTracker.getActiveDataSet().equals(currentDataSet)
                    && !objectTracker.deactivateDataSet(currentDataSet))
            {
                result = false;
            } else if (!objectTracker.destroyDataSet(currentDataSet))
            {
                result = false;
            }

            currentDataSet = null;
        }

        return result;
    }

    @Override
    public boolean doStartTrackers() {
        // Indicate if the trackers were started correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();

        return result;
    }

    @Override
    public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();

        return result;
    }

    @Override
    public void onInitARDone(SampleApplicationException e) {
        if (e == null)
        {

            // Configure OpenGLES
            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            presentador.initApplicationAR();



            try
            {
                vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
                presentador.vuforiaLoaded();
            } catch (SampleApplicationException ex)
            {
                Log.e(LOGTAG, ex.getString());
            }

            boolean result = CameraDevice.getInstance().setFocusMode(
                    CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);


        } else{
            Log.e(LOGTAG, e.getString());
            //mostar error como una toast
            Toast.makeText(activity,e.getString(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onVuforiaUpdate(State state) {

    }

    public void onSurfaceCreated(){
        vuforiaAppSession.onSurfaceCreated();
    }

    public void onSurfaceChanged(int w, int h){
        vuforiaAppSession.onSurfaceChanged(w,h);
    }

}
