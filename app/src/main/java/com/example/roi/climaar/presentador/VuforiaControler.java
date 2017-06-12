package com.example.roi.climaar.presentador;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;

import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Renderer;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.Trackable;
import com.vuforia.TrackerManager;
import com.vuforia.Vec2I;
import com.vuforia.VideoBackgroundConfig;
import com.vuforia.VideoMode;
import com.vuforia.Vuforia;

/**
 * Esta clase controla la activación y desactivación de la librería Vuforia
 * También se encarga de configurar la cámara.
 */

class VuforiaControler {
    private static final String LOGTAG = "VuforiaControler";
    private final static String licenseKey = "AWpyQvb/////AAAAGZx5ZAoRvk6msRcJbt6TCoh4ZH7rhIazxFYCsaoEBwNs9ZZxKeK5+QPR3HTN3Bq3wr1VwQWjMW+75kbrMEG4Jwzu7EAjn4vVw9MZZKuJMn0CThCtV6EeYwRXfsoB8E+vILrv6885BpcZ9ytCaOjZYsKBYS370c739mpOmCjMP8ksBarHN52ZwFjLYW/K6VOHCbSNrHzEOy0AAtPT3RDMbC1crImFUlCIIPCqalThOP9t1llOZlR5WUddD9Ee4A18pV+Pytz24Qtkkpz+eBrVmbA6yzhjDW+O5TpNJID9wUnuvjVvL9ysROBIwofPb6yyyNye/gHlkLSKvR9rabm2bzNf2xS5OPk2ua/3sdoOkDw1";

    private Activity activity;
    private ObjectTracker objectTracker;
    private boolean extendedTrackingActive;
    private DataSet currentDataSet;

    public  VuforiaControler(Activity activity){
        this.activity = activity;
    }

    /**
     * Inicia el tracker de vuforia
     */
    void onCreate(){
        //inicialización Vuforia
        //TODO:(opcional) crear una Task para ejecutar este código
        //y/o al menos mostrar un mensaje de cargando
        int mProgressValue;
        Vuforia.setInitParameters(activity, Vuforia.GL_20, licenseKey);
        do {
            mProgressValue = Vuforia.init();
        } while (mProgressValue >= 0 && mProgressValue < 100);


        //Iniciar tracker
        extendedTrackingActive=true;
        TrackerManager tManager = TrackerManager.getInstance();
        objectTracker = (ObjectTracker) tManager.initTracker(ObjectTracker.getClassType());

        //cargarNuevoMapa datos del tracker: leer archivo que contiene las imágenes a rastrear
        currentDataSet = objectTracker.createDataSet();
        currentDataSet.load("Marker1.xml", STORAGE_TYPE.STORAGE_APPRESOURCE);
        objectTracker.activateDataSet(currentDataSet);
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
    }

    void onResume(){
        Vuforia.onResume();
        //Iniciar camara y trackers
        //Configurar camara
        int camera = CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_BACK;
        CameraDevice.getInstance().init(camera);

        //Configure video background
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        Log.d(LOGTAG, "Tamaño display: x:" + point.x + ", y:" + point.y);
        configureVideoBackground(point.x, point.y);

        if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)) {
            Log.e(LOGTAG, "Unable to enable continuous autofocus");
        }

        CameraDevice.getInstance().selectVideoMode(CameraDevice.MODE.MODE_DEFAULT);
        CameraDevice.getInstance().start();
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        //Iniciar tracker
        objectTracker.start();
    }

    void onPause(){
        Vuforia.onPause();
        objectTracker.stop();
        CameraDevice.getInstance().stop();
    }

    void onDestroy(){
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        Vuforia.deinit();
    }

    /**
     * Igual hay que moverlo a ARRender si no funciona la llamada a Renderer.getInstance()
     * @param mScreenWidth anchura pantalla
     * @param mScreenHeight altura pantalla
     */
    private void configureVideoBackground(int mScreenWidth, int mScreenHeight) {
        CameraDevice cameraDevice = CameraDevice.getInstance();
        VideoMode vm = cameraDevice.getVideoMode(CameraDevice.MODE.MODE_DEFAULT);

        VideoBackgroundConfig config = new VideoBackgroundConfig();
        config.setEnabled(true);
        config.setPosition(new Vec2I(0, 0));

        int xSize = 0, ySize = 0;
        // We keep the aspect ratio to keep the video correctly rendered. If it is portrait we
        // preserve the height and scale width and vice versa if it is landscape, we preserve
        // the width and we check if the selected values fill the screen, otherwise we invert
        // the selection
        //Modo actual: landscape
        xSize = mScreenWidth;
        ySize = (int) (vm.getHeight() * (mScreenWidth / (float) vm
                .getWidth()));

        if (ySize < mScreenHeight) {
            xSize = (int) (mScreenHeight * (vm.getWidth() / (float) vm
                    .getHeight()));
            ySize = mScreenHeight;
        }

        config.setSize(new Vec2I(xSize, ySize));

        //RRender: Configure Video Background : Video (0 , 0), Screen (1080 , 1920), mSize (0 , 1920)
        Log.i(LOGTAG, "Configure Video Background : Video (" + vm.getWidth()
                + " , " + vm.getHeight() + "), Screen (" + mScreenWidth + " , "
                + mScreenHeight + "), mSize (" + xSize + " , " + ySize + ")");

        Renderer.getInstance().setVideoBackgroundConfig(config);
    }
}
