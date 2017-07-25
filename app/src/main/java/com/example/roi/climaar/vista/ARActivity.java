package com.example.roi.climaar.vista;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roi.climaar.R;
import com.example.roi.climaar.modelo.Modelo;
import com.example.roi.climaar.modelo.despacho.Despacho;
import com.example.roi.climaar.presentador.IPresentador;
import com.example.roi.climaar.presentador.Presentador;
import com.vuforia.State;


import android.os.Handler;

/**
 * 
 */
public class ARActivity extends Activity implements
        IVista, IARActivity, Handler.Callback{

    private static final int MSG_TOAST = 1;
    private static final int MSG_TEXTVIEW = 2;

    private static final String LOGTAG = "ARActivity";
    /**
     * Interfaz para comnicarse con el presentador
     */
    private IPresentador iPresentador;

    private RelativeLayout progressBarLayout;
    private RelativeLayout relativeLayoutGL;
    private TextView textViewTopAR;


    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOGTAG,"onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ar);
        progressBarLayout = (RelativeLayout) findViewById(R.id.layoutLoading);
        relativeLayoutGL = (RelativeLayout) findViewById(R.id.relativeLayoutGL);
        textViewTopAR = (TextView) findViewById(R.id.text_view_top_ar);

        mHandler = new Handler(this);

        //Dejamos pantalla encendida
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Recibir intent ubicación seleccionada
        Intent data = getIntent();
        Despacho map = null;
        if(data!=null && data.getExtras()!=null) {
            map = (Despacho) data.getExtras().getSerializable("MAPA");
            if(map==null){
                Toast.makeText(this,"Error al iniciar AR: error al abrir el despacho",Toast.LENGTH_LONG).show();
                finish();
            }
        }
        //añadir contexto al modelo
        Modelo.getInstance().setContext(this);

        //Crear presentador
        iPresentador= new Presentador(this,this, map);

        //iniciar presentador
        iPresentador.onCreate();
    }

    @Override
    protected void onPause() {
        Log.d(LOGTAG,"onPause");
        super.onPause();
        iPresentador.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(LOGTAG,"onResume");
        super.onResume();
        iPresentador.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOGTAG,"onDestroy");
        super.onDestroy();
        iPresentador.onDestroy();
    }

    @Override
    public void onBackPressed() {
        iPresentador.onBackPressed();
    }

    @Override
    public void addGLView(View v) {
        relativeLayoutGL.addView(v, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void mostrarTextoDebug(String text) {
        Message msg = Message.obtain(); // Creates an new Message instance
        msg.obj = text; // Put the string into Message, into "obj" field.
        msg.what = MSG_TEXTVIEW;
        msg.setTarget(mHandler); // Set the Handler
        msg.sendToTarget(); //Send the message
    }


    @Override
    public void mostrarBarraCargando(boolean visible) {
        if(visible)
            progressBarLayout.setVisibility(View.VISIBLE);
        else
            progressBarLayout.setVisibility(View.GONE);
    }

    @Override
    public void mostrarToast(String text) {
        Message msg = Message.obtain(); // Creates an new Message instance
        msg.obj = text; // Put the string into Message, into "obj" field.
        msg.what = MSG_TOAST;
        msg.setTarget(mHandler); // Set the Handler
        msg.sendToTarget(); //Send the message
    }

    @Override
    public MenuOpciones getMenuOpciones() {
        return (MenuOpciones) findViewById(R.id.menuOpciones);
    }

    @Override
    public FloatingActionButton getFAB() {
        return (FloatingActionButton) findViewById(R.id.fab);
    }


    /**
     * Enviar estado de vuforia al presentador
     * @param state estado de vudoria
     */
    @Override
    public float[] updateVuforiaState(State state) {
        return iPresentador.updateVuforiaState(state);
    }

    @Override
    public void loadFiguras() {
        iPresentador.loadFiguras();
    }

    @Override
    public void onSurfaceCreated() {
        iPresentador.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        iPresentador.onSurfaceChanged(width, height);
    }



    @Override
    public boolean handleMessage(Message msg) {
        String text;
        switch (msg.what){
            case MSG_TEXTVIEW:
                text= (String) msg.obj;
                textViewTopAR.setText(text);
                return true;
            case MSG_TOAST:
                text = (String) msg.obj;
                Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

}
