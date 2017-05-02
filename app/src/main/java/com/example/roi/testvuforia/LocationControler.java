package com.example.roi.testvuforia;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;

import com.example.roi.testvuforia.graficos.Mapa.MapaElement;
import com.example.roi.testvuforia.graficos.Mapa.MapaControler;
import com.example.roi.testvuforia.vuforia.ArActivity;
import com.example.roi.testvuforia.vuforia.ArRender;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;

import java.lang.reflect.Array;

import static com.vuforia.TrackableResult.STATUS.*;
import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by roi on 14/12/16.
 *
 * Clase encargada de manejar la posición y orientacion del dipositivo
 * Recibirá los datos desde Vuforia y los transformará a otras representaciones útiles
 * Además procesará la lectura del giroscopio para ayudar a la orientacion si vuforia pierde el rastreo
 */

public class LocationControler implements SensorEventListener{

    //Variables Listener giroscopio
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean listenerRegistered=false;

    ArActivity arActivity;

    private float[] matrix;
    private float[] calibGiroMatrix;//matriz para alinear el giroscopio con el marcador de vudoria
    private Quaternion quaternionGiroscopioListener;//ultima lectura giroscopio


    private boolean firstTracked;//traking realizado y calibración

    private float[] vuforiaMatrix;//Lectura matriz de vuforia
    private float[] lastVuforiaMatrix;//ultima lectura correcta matriz de vuforia


    public LocationControler(Context context, MapaControler mapaControler, ArActivity arActivity){
        matrix = new float[16];
        quaternionGiroscopioListener = new Quaternion();
        vuforiaMatrix = ArRender.identityMatrix.clone();
        lastVuforiaMatrix = ArRender.identityMatrix.clone();

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        this.arActivity = arActivity;
        calibGiroMatrix=ArRender.identityMatrix.clone();
    }

    /**
     * Debe ser llamado cuando necesitamos iniciar el controlador
     */
    public void startControler(){
        if(!listenerRegistered) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);//Una por segundo
            listenerRegistered=true;
        }
    }

    /**
     * Debe ser llamado cuando no se vaya a usar el controlador
     */
    public void stopControler(){
        if (listenerRegistered) {
            mSensorManager.unregisterListener(this);
            listenerRegistered=false;
        }

    }


    /**
     * Función a llamar para actualizar la información sobre la orientación y posición
     * @param state estado obtenido de vuforia: UNKNOWN, TRACKED, EXTENDED_TRACKED
     * @return
     */
    public float[] updateLocation(State state){
        StringBuilder strBuild = new StringBuilder();
        int resultType = UNKNOWN;

        //Obtener estado empleando el primer trackableResult
        if(state.getNumTrackableResults()>0){
            TrackableResult result = state.getTrackableResult(0);
            vuforiaMatrix=Tool.convertPose2GLMatrix(result.getPose())
                    .getData();
            resultType = result.getStatus();
        }
        float[] posCamara;

        switch (resultType) {
            case TRACKED://Imagen con buena precision
                //Guardar giroCalib y posCalib
                //Hace falta invertir matriz vuforia asi que la guardamos y ya invertimos mas tarde
                //de ser necesario
                //tambien guardamos en que estado estaba el giroscopio para poder conocer
                //la diferencia entre vuforia y el giroscopio
                lastVuforiaMatrix = vuforiaMatrix.clone();
                float distancia = (vuforiaMatrix[12]+vuforiaMatrix[13]+vuforiaMatrix[14]);
                if(distancia<50){//Calibramos
                    firstTracked = true;
                    float[] giroMatrix = new float[16];
                    float[] sumaMatrix = new float[16];
                    //lectura rotacion giroscopio
                    SensorManager.getRotationMatrixFromVector(giroMatrix,quaternionGiroscopioListener.toFloat());
                    //lectura rotacion vuforia
                    float[] vuforiaRotMatrix = vuforiaMatrix.clone();
                    vuforiaRotMatrix[12]=0;
                    vuforiaRotMatrix[13]=0;
                    vuforiaRotMatrix[14]=0;
                    //rotamos el giro con la rotacion actual de vuforia, para trasponer y poder deshacer el giro luego
                    Matrix.multiplyMM(sumaMatrix,0,giroMatrix,0,vuforiaRotMatrix,0);
                    Matrix.transposeM(calibGiroMatrix,0,sumaMatrix,0);
                }
                //Return matrixVuforia
                matrix=vuforiaMatrix;
                strBuild.append("Tracked\n");
                posCamara = invertTransformationMatrix(vuforiaMatrix);
                appendCamPos(strBuild,posCamara[12],posCamara[13],posCamara[14]);
                break;

            case EXTENDED_TRACKED://imagen con precision media
                //obtener posCamara
                posCamara = invertTransformationMatrix(vuforiaMatrix);
                matrix = giroQuaternion2VuforiaGL(-posCamara[12],-posCamara[13],-posCamara[14]);
                strBuild.append("ExtendTracking\n");
                appendCamPos(strBuild,posCamara[12],posCamara[13],posCamara[14]);
                break;
            case UNKNOWN:
            default:
                //utilizamos ultima posicion conocida y giro
                if(firstTracked) {
                    //obtener posCamara
                    posCamara = invertTransformationMatrix(matrix);
                    matrix = giroQuaternion2VuforiaGL(-posCamara[12],-posCamara[13],-posCamara[14]);
                    strBuild.append("OnlyGyro\n");
                    appendCamPos(strBuild,posCamara[12],posCamara[13],posCamara[14]);
                } else {
                    strBuild.append("NOT_TRACKED_YET\n");
                    matrix = ArRender.nullMatrix;
                }
                break;
        }

        /*for (int i = 0,k=0; i < 4; i++) {
            for (int j = 0; j < 4; j++,k++) {
                strBuild.append(String.format("%.1f", matrix[k])).append(' ');
            }
            strBuild.append('\n');
        }*/
        arActivity.setTextViewText(strBuild.toString());
        return matrix;
    }



    private void appendCamPos(StringBuilder strBuild, float x, float y, float z){
        strBuild.append("PosCam: X=").append(String.format("%.1f", x)).
                append(" Y=").append(String.format("%.1f", y)).
                append(" Z=").append(String.format("%.1f", z));
    }

    /**
     * Helper invertir matriz
     * @param matrix
     * @return
     */
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


    /**
     * Utilizar rotacion del giroscopio y posicon de camara indicada
     * @param x
     * @param y
     * @param z
     * @return
     */
    private float[] giroQuaternion2VuforiaGL(float x, float y, float z){
        float[] matrixTempt = ArRender.identityMatrix.clone();
        float[] giroMatrixCorrected = new float[16];
        float[] giroMatrix = new float[16];
        float[] scaleMatrix = ArRender.identityMatrix.clone();
        Matrix.scaleM(scaleMatrix,0,-1,-1,-1);
        //utilizamos posicion de vuforia y giro giroscopio
        SensorManager.getRotationMatrixFromVector(giroMatrix,quaternionGiroscopioListener.toFloat());
        //Restar calibracion
        Matrix.multiplyMM(giroMatrixCorrected,0,calibGiroMatrix,0,giroMatrix,0);
        //giroMatrix = scaleMatrix*giroMatrix*scaleMatrix
        float[] tmpMatrix=new float[16];
        Matrix.multiplyMM(tmpMatrix,0,giroMatrixCorrected,0,scaleMatrix,0);
        Matrix.multiplyMM(giroMatrixCorrected,0,scaleMatrix,0,tmpMatrix,0);
        Matrix.transposeM(tmpMatrix,0,giroMatrixCorrected,0);
        Matrix.translateM(matrixTempt,0,x,y,z);

        float[] returnMatrix = new float[16];
        Matrix.multiplyMM(returnMatrix,0,tmpMatrix,0,matrixTempt,0);
        return returnMatrix;
    }

    /**
     * Funcion para recibir los datos de orientación desde el giroscopio
     * @param event
     */
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

    //No usada
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

