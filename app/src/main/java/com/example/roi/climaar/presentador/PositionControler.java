package com.example.roi.climaar.presentador;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.util.Log;


import com.example.roi.climaar.vista.ARRender;
import com.example.roi.climaar.vista.IVista;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;

import static com.vuforia.TrackableResult.STATUS.EXTENDED_TRACKED;
import static com.vuforia.TrackableResult.STATUS.TRACKED;
import static com.vuforia.TrackableResult.STATUS.UNKNOWN;

/**
 * Created by roi on 14/12/16.
 *
 * Clase encargada de manejar la posición y orientacion del dipositivo
 * Recibirá los datos desde Vuforia y los transformará a otras representaciones útiles
 * Además procesará la lectura del giroscopio para ayudar a la orientacion si vuforia pierde el rastreo
 */

public class PositionControler implements SensorEventListener{

    private static final String LOGTAG = "PositionControler";

    //Funciones callback para notificar estado
    private PositionControlerCallback callback;


    public enum TrackStatus{
        TRACKED,
        EXT_TRACKED,
        GIRO_TRACKED,
        NOT_TRACKED
    }

    //Obtener ultimo estado
    private TrackStatus trackStatus;

    //Variables sensores
    private SensorManager mSensorManager;
    private boolean listenerRegistered=false;
    private Sensor mSensorRotation;
    private Sensor mSensorBaro;
    //Ultima lectua giroscopio
    private float[] quatGiroscopioListener;
    //Ultima lectura altimetro
    private float altitud;
    private boolean useBarometer;


    //Calibracion sensores
    private float calibYaw;
    private float altitudCalib;
    private long lastCalibTime;


    //Matrices: vuforia y matrix de return
    private float[] vuforiaMatrix;
    private float[] matrix;

    //Retardo posicion camara buena
    private long lastGoodMatrixTime;
    private float[] lastGoodPosCamera;
    private float[] lastGoodPosCameraNext;

    //VARIABLES PRESION
    //Ultimo valor de presion, usado filtro de paso bajo, inicializado a presion en 0m nivel del mar
    private float lastPresure;
    //Variable que controla si la calibracion del barometro esta activada o no
    private boolean presureCalib = false;
    //Contador del repeticiones de calibracion, hasta llegar a N_CALIB
    private int iPresureCalib = 0;
    //Sumatorio de las presiones parciales, para obtener la media
    private float presuresum = 0;
    //Numero de repeticiones necesarias para realizar la calibracion
    private final static int N_CALIB = 10;
    //Posicion de la camara en el momento de iniciar la calibracion
    private float posCamaraCalib;


    private boolean firstTracked=false;//traking realizado y calibración
    private boolean calibrated=false;//traking realizado y calibración



    public PositionControler(Context context, PositionControlerCallback callback, boolean useBarometer){
        matrix = new float[16];
        lastGoodPosCamera = new float[3];
        lastGoodPosCameraNext = new float[3];

        quatGiroscopioListener = new float[4];
        vuforiaMatrix = ARRender.identityMatrix.clone();

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        if(useBarometer)
            mSensorBaro = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        this.callback = callback;
        this.useBarometer = useBarometer;
    }

    /**
     * Debe ser llamado cuando necesitamos iniciar el controlador
     * Iniciar listeners
     * Resetear calibraciones
     * Status = notTracked
     */
    public void onResume(){
        if(!listenerRegistered) {
            mSensorManager.registerListener(this, mSensorRotation, SensorManager.SENSOR_DELAY_GAME);//Una por segundo
            if(useBarometer)
                mSensorManager.registerListener(this, mSensorBaro, SensorManager.SENSOR_DELAY_GAME);//Una por segundo
            listenerRegistered=true;
        }
        resetVars();
    }

    private void resetVars(){
        firstTracked=false;
        calibrated = false;
        trackStatus = TrackStatus.NOT_TRACKED;
        lastPresure=1013.25f;
    }

    /**
     * Debe ser llamado cuando no se vaya a usar el controlador
     * De-registrar listeners
     */
    public void onPause(){
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
        int resultType = UNKNOWN;

        //Obtener estado empleando el primer trackableResult
        if(state.getNumTrackableResults()>0){
            TrackableResult result = state.getTrackableResult(0);
            vuforiaMatrix=Tool.convertPose2GLMatrix(result.getPose())
                    .getData();
            resultType = result.getStatus();
        }
        float[] posCamara;
        float cameraY;


        switch (resultType) {
            case TRACKED://Imagen con buena precision
                long currentTime = System.currentTimeMillis();
                float distancia = (vuforiaMatrix[12]+vuforiaMatrix[13]+vuforiaMatrix[14]);
                if(distancia<80){//Calibramos
                    float anguloZVuf = calculateYawVuf();
                    if(anguloZVuf>-10f && anguloZVuf<10f){
                        if(calibrated && ((currentTime-lastCalibTime)>10000) ){//Calibrar cada 10Seg
                            //Calibrar altimetro

                            if(useBarometer) {
                                posCamara = invertTransformationMatrix(vuforiaMatrix);
                                //Comenzar a calibrar
                                empezarCalibracionAltitudSensor(posCamara[13]);
                            }

                            //Calibrar yaw giroscopio
                            lastCalibTime = currentTime;
                            float anguloZGiro = calculateYawGiro();
                            calibYaw = anguloZGiro+anguloZVuf;

                            firstTracked = true;
                            Log.d(LOGTAG,"Calibrated giro, yaw="+calibYaw);
                            if(callback!=null) callback.giroCalibrado();
                        }else if(!calibrated){
                            calibrated = true;
                            lastCalibTime = currentTime-9000;//1a vez->Calibrar en 1 segundo
                        }
                    }
                }

                //Almacenar posicion camara para utilizar en 0.5seg
                //de esta forma evitamos errores de posicion al perder el tracking
                updateLastGoodPosCamera();
                //Return matrixVuforia
                matrix=vuforiaMatrix;
                if(trackStatus!=TrackStatus.TRACKED){
                    trackStatus = TrackStatus.TRACKED;
                    if(callback!=null) callback.onStateChanged(TrackStatus.TRACKED);
                }
                break;

            case EXTENDED_TRACKED://imagen con precision media
                //obtener posCamara
                posCamara = invertTransformationMatrix(vuforiaMatrix);
                updateLastGoodPosCamera(posCamara);
                cameraY = useBarometer? (altitudCalib-altitud) : -posCamara[13];
                matrix = giroQuaternion2VuforiaGL(-posCamara[12],cameraY,-posCamara[14]);
                if(trackStatus!=TrackStatus.EXT_TRACKED){
                    trackStatus = TrackStatus.EXT_TRACKED;
                    if(callback!=null) callback.onStateChanged(TrackStatus.EXT_TRACKED);
                }
                break;
            case UNKNOWN:
            default:
                //utilizamos ultima posicion conocida y giro
                if(firstTracked) {
                    //obtener posCamara
                    cameraY = useBarometer? (altitudCalib-altitud) : lastGoodPosCamera[1];
                    matrix = giroQuaternion2VuforiaGL(lastGoodPosCamera[0],cameraY,lastGoodPosCamera[2]);
                    if(trackStatus!=TrackStatus.GIRO_TRACKED){
                        trackStatus = TrackStatus.GIRO_TRACKED;
                        if(callback!=null) callback.onStateChanged(TrackStatus.GIRO_TRACKED);
                    }
                } else {
                    if(trackStatus!=TrackStatus.NOT_TRACKED){
                        trackStatus = TrackStatus.NOT_TRACKED;
                        if(callback!=null) callback.onStateChanged(TrackStatus.NOT_TRACKED);
                    }
                    matrix = ARRender.nullMatrix;
                }
                break;
        }
        return matrix.clone();
    }


    /**
     * Almacena la posicion de la camara cada 0.5segundos
     * Y la anterior almacenada pasa a ser la ultima posicion valida
     * @param posCamara posicion a almacenar
     */
    private void updateLastGoodPosCamera(float[] posCamara) {
        long currentTime = System.currentTimeMillis();
        if((currentTime-lastGoodMatrixTime)>500){//guardar matrix cada 0,5seg
            lastGoodMatrixTime=currentTime;
            lastGoodPosCamera=lastGoodPosCameraNext;
            lastGoodPosCameraNext = new float[3];
            lastGoodPosCameraNext[0] = -posCamara[12];
            lastGoodPosCameraNext[1] = -posCamara[13];
            lastGoodPosCameraNext[2] = -posCamara[14];
        }
    }

    /**
     * Almacena la posicion de la camara cada 0.5segundos utilizando la matriz dada
     * Y la anterior almacenada pasa a ser la ultima posicion valida
     */
    private void updateLastGoodPosCamera() {
        long currentTime = System.currentTimeMillis();
        if((currentTime-lastGoodMatrixTime)>500){//guardar matrix cada 0,5seg
            float[] posCamara = invertTransformationMatrix(vuforiaMatrix);
            lastGoodMatrixTime=currentTime;
            lastGoodPosCamera=lastGoodPosCameraNext;
            lastGoodPosCameraNext = new float[3];
            lastGoodPosCameraNext[0] = -posCamara[12];
            lastGoodPosCameraNext[1] = -posCamara[13];
            lastGoodPosCameraNext[2] = -posCamara[14];
        }
    }


    /**
     * Calcula el angulo de yaw del giroscopio respecto a su centro
     * @return angulo yaw calculado en grados
     */
    private float calculateYawGiro() {
        //Calcular anguloZ del giroscopio respecto a su origen (0,0,0)
        float[] q = new float[4];
        synchronized (quatGiroscopioListener) {
            q[0]=quatGiroscopioListener[2]; //X=Z
            q[1]=quatGiroscopioListener[1]; //Y
            q[2]=-quatGiroscopioListener[0]; //Z=-X
            q[3]=quatGiroscopioListener[3]; //W
        }
        float ysqr = q[1] * q[1];
        float t0 = +2.0f * (q[3] * q[0] + q[1] * q[2]);
        float t1 = +1.0f - 2.0f * (q[0] * q[0] + ysqr);
        return (float) Math.atan2(t0, t1)*57.2957795f;
    }

    /**
     * Utilizando el atributo vuforiaMatrix, obtiene el angulo yaw respecto al objeto de referencia
     * @return angulo yaw calculado en grados
     */
    private float calculateYawVuf() {
        //Calcular anguloZ de vuforia respecto a (0,0,0)
        float ang;
        ang = (float)Math.atan2(-vuforiaMatrix[8], vuforiaMatrix[10])*57.2957795f;
        ang+=180f;
        if(ang>180f) ang-=360f;
        return ang;
    }

    /**
     * Funcion que invierte una matriz
     * @param matrix matriz a invertir, de tamaño 16
     * @return nueva matriz invertida
     */
    private static float[] invertTransformationMatrix(float[] matrix){
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


    private final static float[] correctGiro = {
            1f,0f,0f,0f,
            0f,0f,1f,0f,
            0f,-1f,0f,0f,
            0f,0f,0f,1f
    };

    /**
     * Calcula la matriz de openGL utilizando la rotacion del giroscopio actual
     * y posicion de camara indicada
     * @param x posicion eje X de la camara
     * @param y posicion eje Y de la camara
     * @param z posicion eje Z de la camara
     * @return matriz valida para emplear en openGL usando los ejes de Vuforia
     */
    private float[] giroQuaternion2VuforiaGL(float x, float y, float z){
        float[] giroMatrixCorrected = new float[16];
        float[] giroMatrix = new float[16];
        float[] scaleMatrix = ARRender.identityMatrix.clone();
        Matrix.scaleM(scaleMatrix,0,-1,-1,-1);
        float[] tmpMatrix=new float[16];
        float[] matrixTempt = ARRender.identityMatrix.clone();

        float[] rotateYaw = ARRender.identityMatrix.clone();
        Matrix.rotateM(rotateYaw,0,calibYaw,0,0,1);

        //utilizamos posicion de vuforia y giro giroscopio
        SensorManager.getRotationMatrixFromVector(giroMatrix,quatGiroscopioListener);

//        //Restar calibracion

        Matrix.multiplyMM(tmpMatrix,0,correctGiro,0,rotateYaw,0);
        Matrix.multiplyMM(giroMatrixCorrected,0,tmpMatrix,0,giroMatrix,0);



        //giroMatrix = scaleMatrix*giroMatrix*scaleMatrix
        Matrix.multiplyMM(tmpMatrix,0,giroMatrixCorrected,0,scaleMatrix,0);
        Matrix.multiplyMM(giroMatrixCorrected,0,scaleMatrix,0,tmpMatrix,0);
        Matrix.transposeM(tmpMatrix,0,giroMatrixCorrected,0);

        Matrix.translateM(matrixTempt,0,x,y,z);

        float[] returnMatrix = new float[16];
        Matrix.multiplyMM(returnMatrix,0,tmpMatrix,0,matrixTempt,0);
        return returnMatrix;
    }




    /**
     * Comienza la calibración del barometro repecto a la altitud dada de la camara
     * @param posCamara altitud de la camara en coordenadas de vuforia
     */
    private void empezarCalibracionAltitudSensor(float posCamara){
        this.posCamaraCalib = posCamara;
        iPresureCalib =0;
        presuresum = 0;
        presureCalib=true;
    }

    /**
     * Solo llamar a este metodo cuando haya nuevos valores de altitud del barometro
     * desde el método onSensorChanged y solo si presureCalib==true
     *
     * Este método realizara la media de 10 presiones para calcular el parametro de calibracion
     * respecto a la altitud de la camara de vuforia.
     */
    private void calibAltitudSensor(float pressure){
        iPresureCalib++;
        presuresum+=pressure;
        if(iPresureCalib >(N_CALIB-1)){//sobre medio segundo de 1 a 10
            float altitudMedia = 100f*SensorManager.getAltitude(
                    SensorManager.PRESSURE_STANDARD_ATMOSPHERE,(presuresum/N_CALIB));
            altitudCalib = posCamaraCalib+altitudMedia;
            presureCalib=false;
            Log.d(LOGTAG,"Barometro calibrado, val="+altitudCalib);
            if(callback!=null) callback.barometroCalibrado();
        }
    }

    /**
     * Funcion para recibir los datos de orientación desde el giroscopio
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            synchronized (quatGiroscopioListener) {
                quatGiroscopioListener[0] = event.values[1];
                quatGiroscopioListener[1] = event.values[0];
                quatGiroscopioListener[2] = event.values[2];
                quatGiroscopioListener[3] = event.values[3];
            }
        }else if(type == Sensor.TYPE_PRESSURE){
            float filteredPresure = 0.01f*event.values[0]+0.99f*lastPresure;
            lastPresure = filteredPresure;
            //convertir a centimetros
            altitud = 100f*SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE,filteredPresure);
            if(presureCalib){
                calibAltitudSensor(event.values[0]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public boolean isUseBarometer() {
        return useBarometer;
    }

    public void setUseBarometer(boolean useBarometer) {
        if(useBarometer&& !this.useBarometer){//Encender use barometer
            //Registrar sensor
            if(mSensorBaro==null){
                mSensorBaro = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            }
            mSensorManager.registerListener(this, mSensorBaro, SensorManager.SENSOR_DELAY_GAME);//Una por segundo

            resetVars();
            this.useBarometer = true;//habilitamos uso del barometro
        }else if(!useBarometer&& this.useBarometer){//Apagar use barometer
            this.useBarometer = false;//Cancelamos uso del barometro
            mSensorManager.unregisterListener(this,mSensorBaro);
        }
    }
}

