package com.example.roi.testvuforia.vuforia;

import android.app.Activity;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.example.roi.testvuforia.LocationControler;
import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.Mapa.MapaControler;
import com.example.roi.testvuforia.graficos.MiGLSurfaceView;
import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.Trackable;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;


public class ArActivity extends Activity implements View.OnClickListener{

    private static final String LOGTAG = "ArActivity";

    private final static String licenseKey = "AWpyQvb/////AAAAGZx5ZAoRvk6msRcJbt6TCoh4ZH7rhIazxFYCsaoEBwNs9ZZxKeK5+QPR3HTN3Bq3wr1VwQWjMW+75kbrMEG4Jwzu7EAjn4vVw9MZZKuJMn0CThCtV6EeYwRXfsoB8E+vILrv6885BpcZ9ytCaOjZYsKBYS370c739mpOmCjMP8ksBarHN52ZwFjLYW/K6VOHCbSNrHzEOy0AAtPT3RDMbC1crImFUlCIIPCqalThOP9t1llOZlR5WUddD9Ee4A18pV+Pytz24Qtkkpz+eBrVmbA6yzhjDW+O5TpNJID9wUnuvjVvL9ysROBIwofPb6yyyNye/gHlkLSKvR9rabm2bzNf2xS5OPk2ua/3sdoOkDw1";
    private MiGLSurfaceView glView;
    private ArRender arRender;
    private LocationControler locationControler;
    private MapaControler mapaControler;

    private ToggleButton btnExtendedTracking;
    private Button btnVolverRight;
    private Button btnSetObj;
    private TextView textViewTop;


    private Handler handler;
    private final static int MSG_UPDATE_TEXTVIEW = 1;

    private ObjectTracker objectTracker;
    private boolean extendedTrackingActive;
    private DataSet currentDataSet;

    private int camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        createHandler();


        //ASIGNAR INTERFACE
        btnExtendedTracking = (ToggleButton) findViewById(R.id.toggleButtonExtTracking);
        btnSetObj = (Button) findViewById(R.id.button_ar_put_obj);
        btnSetObj.setOnClickListener(this);
        btnVolverRight = (Button) findViewById(R.id.button_ar_back);
        btnVolverRight.setOnClickListener(this);
        btnExtendedTracking.setOnClickListener(this);
        textViewTop = (TextView) findViewById(R.id.text_view_top_ar);

        //Dejamos pantalla encendida
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        //inicialización Vuforia
        //TODO:(opcional) crear una Task para ejecutar este código
        //y/o al menos mostrar un mensaje de cargando
        int mProgressValue;
        Vuforia.setInitParameters(this, Vuforia.GL_20, licenseKey);
        do {
            mProgressValue = Vuforia.init();
        } while (mProgressValue >= 0 && mProgressValue < 100);


        //Iniciar tracker
        extendedTrackingActive=false;
        TrackerManager tManager = TrackerManager.getInstance();
        objectTracker = (ObjectTracker) tManager.initTracker(ObjectTracker.getClassType());

        //cargar datos del tracker: leer archivo que contiene las imágenes a rastrear
        currentDataSet = objectTracker.createDataSet();
        currentDataSet.load("Prueba.xml", STORAGE_TYPE.STORAGE_APPRESOURCE);
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



        //Crear instancias de MapaControler, LocationControler
        mapaControler = new MapaControler("Mapa",this);
        locationControler=new LocationControler(this,mapaControler);
        glView = new MiGLSurfaceView(this);
        arRender = new ArRender(this,locationControler,mapaControler);
        glView.setRenderer(arRender);
        glView.setOnTouchInterface(arRender);

        //Hay que añadirlo antes de encender el video y de configurar el background (en el sample primero configura el background? wtf)
        RelativeLayout relLayout = (RelativeLayout) findViewById(R.id.relativeLayoutGL);
        relLayout.addView(glView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraDevice.getInstance().stop();

        if (glView != null) {
            glView.setVisibility(View.INVISIBLE);
            glView.onPause();
        }

        locationControler.onStop();
        Vuforia.onPause();
        objectTracker.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Vuforia.onResume();
        locationControler.onStart();

        if (glView != null) {
            glView.setVisibility(View.VISIBLE);
            glView.onResume();
        }

        //Iniciar camara y trackers
        //Configurar camara
        camera = CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_BACK;
        CameraDevice.getInstance().init(camera);
        //Configure video background
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        Log.d(LOGTAG, "Tamaño display: x:" + point.x + ", y:" + point.y);
        arRender.configureVideoBackground(point.x, point.y);
        if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)) {
            Log.e(LOGTAG, "Unable to enable continuous autofocus");
        }

        CameraDevice.getInstance().selectVideoMode(CameraDevice.MODE.MODE_DEFAULT);
        CameraDevice.getInstance().start();
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        //Iniciar tracker
        objectTracker.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        Vuforia.deinit();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.toggleButtonExtTracking:
                if(btnExtendedTracking.isChecked()){
                    cambiarExtendedTracking(true);
                }else{
                    cambiarExtendedTracking(false);
                }
                // do stuff
                break;
            case R.id.button_ar_back:
                finish();//Terminamos activity
                break;
            case R.id.button_ar_put_obj:
                //TODO: put obj;
                locationControler.nuevaColision();
                break;
        }
    }


    private void cambiarExtendedTracking(boolean encender){
        extendedTrackingActive = encender;
        for (int tIdx = 0; tIdx < currentDataSet.getNumTrackables(); tIdx++) {
            Trackable trackable = currentDataSet.getTrackable(tIdx);
            if(extendedTrackingActive){
                trackable.startExtendedTracking();
            }else{
                trackable.stopExtendedTracking();
            }
        }
    }


    private void createHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_UPDATE_TEXTVIEW:{
                        String str = (String)msg.obj;
                        textViewTop.setText(str);
                    }
                }

            }
        };
    }

    // An async task to initialize Vuforia asynchronously.
    private class InitVuforiaTask extends AsyncTask<Void, Integer, Boolean> {
        Activity activity;
        int mProgressValue;
        public InitVuforiaTask(Activity activity){
            this.activity=activity;
        }

        protected Boolean doInBackground(Void... params){
            Vuforia.setInitParameters(activity, Vuforia.GL_20, licenseKey);
            do
            {
                // Vuforia.init() blocks until an initialization step is
                // complete, then it proceeds to the next step and reports
                // progress in percents (0 ... 100%).
                // If Vuforia.init() returns -1, it indicates an error.
                // Initialization is done when progress has reached 100%.
                mProgressValue = Vuforia.init();

                // Publish the progress value:
                publishProgress(mProgressValue);

                // We check whether the task has been canceled in the
                // meantime (by calling AsyncTask.cancel(true)).
                // and bail out if it has, thus stopping this thread.
                // This is necessary as the AsyncTask will run to completion
                // regardless of the status of the component that
                // started is.
            } while (!isCancelled() && mProgressValue >= 0
                    && mProgressValue < 100);

            return (mProgressValue > 0);
        }

        protected void onProgressUpdate(Integer... values)
        {
            // Do something with the progress value "values[0]", e.g. update
            // splash screen, progress bar, etc.
        }

        protected void onPostExecute(Boolean result){

        }
    }
}
