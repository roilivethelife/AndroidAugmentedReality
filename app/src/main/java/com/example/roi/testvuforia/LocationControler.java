package com.example.roi.testvuforia;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;

import com.example.roi.testvuforia.graficos.Mapa.MapaElement;
import com.example.roi.testvuforia.graficos.Mapa.MapaControler;
import com.example.roi.testvuforia.vuforia.ArRender;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;

import static com.vuforia.TrackableResult.STATUS.*;
import static java.lang.Math.abs;

/**
 * Created by roi on 14/12/16.
 */

public class LocationControler implements SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean listenerRegistered=false;

    private MapaControler mapaControler;

    private float[] lastMatrixCamara;
    private Quaternion quaternionGiroscopioListener;
    private Quaternion quaternionGiroscopioCalib;
    float angle1=0;
    float angle2=0;

    private boolean nuevaColision=false;
    private boolean firstTracked;

    private float[] vuforiaMatrix;
    private float[] vuforiaMatrixCalib;

    public LocationControler(Context context, MapaControler mapaControler){
        this.mapaControler=mapaControler;
        lastMatrixCamara = new float[16];
        quaternionGiroscopioListener = new Quaternion();
        quaternionGiroscopioCalib = new Quaternion();
        vuforiaMatrix = ArRender.identityMatrix.clone();
        vuforiaMatrixCalib = ArRender.identityMatrix.clone();

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
    }

    public void onStart(){
        if(!listenerRegistered) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);//Una por segundo
            listenerRegistered=true;
        }
    }

    public void onStop(){
        if (listenerRegistered) {
            mSensorManager.unregisterListener(this);
            listenerRegistered=false;
        }

    }

    public static float[] invertTransformationMatrix(float[] matrix){
        if (matrix.length!=16) throw new RuntimeException("Matrix length!=16");
        float[] ret = matrix.clone();
        //1 Trasponer R
        ret[1]=matrix[4];
        ret[4]=matrix[1];
        ret[2]=matrix[8];
        ret[8]=matrix[2];
        ret[6]=matrix[9];
        ret[9]=matrix[6];
        //2: T = -Rt*T
        ret[12]=-(ret[0]*matrix[12]+ret[4]*matrix[13]+ret[8]*matrix[14]);
        ret[13]=-(ret[1]*matrix[12]+ret[5]*matrix[13]+ret[9]*matrix[14]);
        ret[14]=-(ret[2]*matrix[12]+ret[6]*matrix[13]+ret[10]*matrix[14]);
        return ret;
    }

    public void setAngle(float x,float z){
        this.angle1 = x;
        this.angle2 = z;
    }

    public float[] onFrame(State state){
        int resultType = UNKNOWN;

        int numResults = state.getNumTrackableResults();
        for (int tIdx = 0; tIdx < numResults; tIdx++) {
            TrackableResult result = state.getTrackableResult(tIdx);
            vuforiaMatrix=Tool.convertPose2GLMatrix(result.getPose())
                    .getData();
            resultType = result.getStatus();
        }
        float[] returnMatrix = null;
        switch (resultType){
            case TRACKED:
                firstTracked = true;
                //Guardar posicion giroscopio correcta y giro matrix vuforia correcta
                quaternionGiroscopioCalib = quaternionGiroscopioListener.clone();
                vuforiaMatrixCalib = vuforiaMatrix.clone();
                lastMatrixCamara= vuforiaMatrix;
//                float[] cameraMatrix = invertTransformationMatrix(vuforiaMatrix);
//                arActivityInteface.setTextViewText(cameraMatrix[12]+" "+cameraMatrix[13]+" "+cameraMatrix[14]);
                break;
            case EXTENDED_TRACKED:
            case UNKNOWN:
            default:
                //utilizamos ultima posicion conocida y giro
                //if(firstTracked){
                    //SensorManager.getRotationMatrixFromVector();
                    float[] calibMatrix = new float[16];
                    /*Quaternion quatGiroCamara = quaternionGiroscopioCalib.invertQuaternionCopy();
                    quatGiroCamara.mul(quaternionGiroscopioListener);
                    float[] calibMatrix = new float[16];
                    SensorManager.getRotationMatrixFromVector(calibMatrix,quatGiroCamara.toFloat());*/

                    //invertimos ultima matriz de cámara->Utilizar posicion de esta
                    //float[] cameraMatrixVuforia = invertTransformationMatrix(vuforiaMatrix);
                    //invertimos matriz de calibracion->Utilizar giro de esta
                    //float[] cameraMatrixVuforiaCalib = invertTransformationMatrix(vuforiaMatrixCalib);
                    float[] cameraMatrixVuforiaCalib = ArRender.identityMatrix.clone();
                    //copiamos posicion

                    cameraMatrixVuforiaCalib[12]=0;//cameraMatrixVuforia[12];
                    cameraMatrixVuforiaCalib[13]=0;//cameraMatrixVuforia[13];
                    cameraMatrixVuforiaCalib[14]=35;//cameraMatrixVuforia[14];
                    Matrix.rotateM(cameraMatrixVuforiaCalib,0,180,1,0,0);
                    //en cameraMatrixVuforiaCalib esta la matrix de la camara con la ultima posicion conocida en T1
                    //y con el ultimo giro conocido en T0
                    //ahora giraremos esta matriz segun la diferencia de quaternion de T0 a T2(ahora)
                    //y tendremos la matriz con el giro actualizado según el giroscopio

                    Quaternion q = new Quaternion();
                    angle2=-angle2;
                    q.setEulerAngles(angle1, angle2,0);
                    //arActivityInteface.setTextViewText("Angulos: 1="+angle1+" 2="+angle2+"\nquat="+q.toString(2));
                    SensorManager.getRotationMatrixFromVector(calibMatrix,q.toFloat());

                    float[] matrixfinal = new float[16];
                    Matrix.multiplyMM(matrixfinal,0,cameraMatrixVuforiaCalib,0,calibMatrix,0);
                    //Matrix final es la matrix de la camara con los giros actualizados segun el giroscopio

                    lastMatrixCamara= invertTransformationMatrix(matrixfinal);
                //}else{
                //    lastMatrixCamara= ArRender.identityMatrix;
                //}
        }
        if(nuevaColision){
            float[] camInv = invertTransformationMatrix(lastMatrixCamara);
            float[] camPos = new float[3];
            float[] camDir = new float[3];
            float[] colPoint = new float[3];
            camPos[0]=lastMatrixCamara[12];
            camPos[1]=lastMatrixCamara[13];
            camPos[2]=lastMatrixCamara[14];
            camDir[0]=lastMatrixCamara[8];
            camDir[1]=lastMatrixCamara[9];
            camDir[2]=lastMatrixCamara[10];
            if(mapaControler.colisionRayoPared(camPos,camDir,colPoint)){
                MapaElement mapaElement = mapaControler.getColisionElement();
                mapaElement.pos[0]=colPoint[0];
                mapaElement.pos[1]=colPoint[1];
                mapaElement.pos[2]=colPoint[2];
                mapaElement.visible=true;
            }
            nuevaColision=false;
        }


        return lastMatrixCamara;
    }

    public void nuevaColision(){
        nuevaColision=true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()== Sensor.TYPE_GAME_ROTATION_VECTOR) {
            //quaternionGiroscopioListener.set(event.values);
            quaternionGiroscopioListener.set(event.values[1],event.values[0],event.values[2],event.values[3]);
            /*float[] quaternion = event.values.clone();
            float[] conjugate = invertQuaternion(quaternion);
            float[] mult = multQuaternion(quaternion,conjugate);

            Log.d("Location","Quaternion:"+floatVectorToString(quaternion)+"\n" +
                    "Conjugate:"+floatVectorToString(conjugate)+"\n"+
                    "Mult:"+floatVectorToString(mult)+"\n");*/


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

