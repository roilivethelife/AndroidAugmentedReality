package com.example.roi.testvuforia.graficos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;

import com.example.roi.testvuforia.Quaternion;
import com.example.roi.testvuforia.graficos.ObjLoader.ObjReader;
import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.figuras.Obj;
import com.example.roi.testvuforia.graficos.figuras.Plano;
import com.example.roi.testvuforia.graficos.figuras.Triangulo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by roi on 6/11/16.
 */

public class MiGLRender implements GLSurfaceView.Renderer, OnTouchInterface, SensorEventListener {


    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mPVMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private boolean listenerRegistered=false;
    private Quaternion quaternionGiroscopioListener;
    private Quaternion quaternionGiroscopioCalib;
    private long lastMs;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    Context context;

    public MiGLRender(Context context) {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        quaternionGiroscopioListener=new Quaternion();
        quaternionGiroscopioCalib = new Quaternion();
    }

    public void onStart(){
        if(!listenerRegistered) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);//Una por segundo
            listenerRegistered=true;
            lastMs = SystemClock.uptimeMillis();
        }
    }

    public void onStop(){
        if (listenerRegistered) {
            mSensorManager.unregisterListener(this);
            listenerRegistered=false;
        }

    }

    private Shader shader;
    private Obj obj;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        shader = new Shader(context);
        checkGLError("Fin surface created");
        ObjReader coche = new ObjReader(context, R.raw.cubo);
        obj = coche.getObjeto();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        //Método de proyección
        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        checkGLError("Fin surface changed");
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        //Limiamos depth y color buffer
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);//limpiamos de color azul


        // Set the camera position (View matrix)
        //Camara en 0,0,-3
        //Mirando a 0,0,0
        //UP Y+
        //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, 0, 0, -3);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mPVMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Aplicar matriz Proj*View al shader
        GLES20.glUniformMatrix4fv(shader.getmPVMatrixHandle(), 1, false, mPVMatrix, 0);


        //dibujamos:
        Quaternion quatGiroCamara = quaternionGiroscopioCalib.invertQuaternionCopy();
        quatGiroCamara.mul(quaternionGiroscopioListener);
        long ms = SystemClock.uptimeMillis();

        if((ms-lastMs)>500) {
            Log.d("GLRender", "Listener="+quaternionGiroscopioListener.toString(2)+"\n"+
                    "Calib="+quaternionGiroscopioCalib.toString(2)+"\n"+
                            "Mult="+quatGiroCamara.toString(2));
            lastMs=ms;
        }

        SensorManager.getRotationMatrixFromVector(modelMatrix,quatGiroCamara.toFloat());

        //Matrix.rotateM(modelMatrix,0,-anguloY,1.0f,0,0);
        //Matrix.rotateM(modelMatrix,0,anguloX,0,1.0f,0.0f);

        //Matrix.setIdentityM(modelMatrix, 0);
        //Matrix.rotateM(modelMatrix,0,-anguloY,1.0f,0,0);
        //Matrix.rotateM(modelMatrix,0,anguloX,0,1.0f,0.0f);
        Matrix.translateM(modelMatrix,0,0,0,-0.5f);
        GLES20.glUniformMatrix4fv(shader.getmModelMatrixHandle(), 1, false, modelMatrix, 0);
        //plano.dibujar();
        obj.dibujar(shader);
        checkGLError("Fin dibujar");

    }

    public static void checkGLError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("MyApp", op + ": glError " + error);
        }
    }


    private float mPreviousX;
    private float mPreviousY;
    private final static float sensibility = 4.0f;
    private float anguloY;
    private float anguloX;
    @Override
    public boolean onCustomTouchEvent(MotionEvent event) {
        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                quaternionGiroscopioCalib = quaternionGiroscopioListener.clone();

                float deltaX = (x - mPreviousX) / sensibility;
                float deltaY = (y - mPreviousY) / sensibility;

                anguloY += deltaY;
                anguloX += deltaX;
                anguloY%=360;
                anguloX%=360;
            }

            mPreviousX = x;
            mPreviousY = y;

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(/*event.sensor.getType()== Sensor.TYPE_ROTATION_VECTOR*/event.values.length>=4) {
            quaternionGiroscopioListener.set(event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}