/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmofusion.util;

/**
 *
 * @author roi
 */
public class SensorCalc {

    //Aceleracion actual en cada uno de los 3 ejes
    private float[] aceleracion = new float[3];
    //Aceleracion anterior en cada uno de los 3 ejes
    public float[] filteredAcel = new float[3];
    private int acelCount = 0;
    //Velocidad actual en cada uno de los 3 ejes
    private float[] velocidad = new float[3];
    //Velocidad anterior en cada uno de los 3 ejes
    private float[] velocidadAnt = new float[3];
    //Posicion actual en cada uno de los 3 ejes
    private float[] posicion = new float[3];

    //numero de veces que la aceleracion no pasa la ventana
    private float secZero = 0;
    private float lastStepSec = 0;

    //Giro en cada uno de los tres ejes
    private float[] giro = new float[3];

    private static final float NS2S = 1.0f / 1000000000.0f;
    private long timestamp;
    private float sElapsed = 0;
    private boolean run;

    //Calibration variables
    private boolean calibrate;
    private int calibrationCount = 0;
    private float[] calibVal = new float[3];//TODO: implementar apfloat
    //private Apfloat[] calibVal = new Apfloat[3];

    public SensorCalc() {
        calibrate = false;
    }

    private void calibration(SensorEvent event) {
        if (calibrationCount < 120) {
            calibrationCount++;
            calibVal[0] += event.values[0];
            calibVal[1] += event.values[1];
            calibVal[2] += event.values[2];
        } else {
            calibrate = false;
            calibrationCount = 0;
            calibVal[0] /= 120;
            calibVal[1] /= 120;
            calibVal[2] /= 120;
        }
    }

    public void eventoLinearAcel(SensorEvent event) {

        final int NUM_MUESTRAS = 3;
        //1: girar aceleraciones segun angulos ( se puede obviar si no giramos el movil
        //2: integrar aceleracion y calcular nueva velocidad
        //3; integrar velocidad y calcular nueva posicion
        //Primero: recogemos 32 muestras de aceleracion
        /*
        acelCount++;
        aceleracion[0] += event.values[0];
        aceleracion[1] += event.values[1];
        aceleracion[2] += event.values[2];
        if (acelCount < NUM_MUESTRAS) {
            return;
        }
        acelCount = 0;
        aceleracion[0] /= NUM_MUESTRAS;
        aceleracion[1] /= NUM_MUESTRAS;
        aceleracion[2] /= NUM_MUESTRAS;*/

        //Eliminar 0 refence, (o si)
        /*aceleracion[0]-=calibVal[0];
        aceleracion[1]-=calibVal[1];
        aceleracion[2]-=calibVal[2];*/
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S; //diferencia de tiempo en segundos

            aceleracion[0] = event.values[0];
            aceleracion[1] = event.values[1];
            aceleracion[2] = event.values[2];

            if (lastStepSec > 0.350 && aceleracion[2] < filteredAcel[2] && filteredAcel[2] > 5.0f) {
                lastStepSec = 0;
            } else {
                lastStepSec += dT;
            }

            //Filtrar aceleracion
            for (int i = 0; i < aceleracion.length; i++) {
                boolean zero = false;
                float dif = aceleracion[i] - filteredAcel[i];
                float absFil = Math.abs(filteredAcel[i]);
                if (absFil < 0.01 && Math.abs(aceleracion[i]) < 0.1) {
                    filteredAcel[i] = 0;
                } else if (Math.abs(dif) > Math.abs(filteredAcel[i])) {
                    filteredAcel[i] = aceleracion[i];
                } else {
                    filteredAcel[i] += dif;
                    if (Math.abs(filteredAcel[i]) < 0.1) {
                        filteredAcel[i] = 0;
                    }
                }
            }

            velocidad[0] += filteredAcel[0] * dT; //m/s^2*s -> m/s
            velocidad[1] += filteredAcel[1] * dT; //m/s^2*s -> m/s
            velocidad[2] += filteredAcel[2] * dT; //m/s^2*s -> m/s

            if (lastStepSec > 0.450) {
                velocidad[0] = 0;
                velocidad[1] = 0;
                velocidad[2] = 0;
            } else {
                /*for (int i = 0; i < 3; i++) {
                    if (velocidad[i] > 1.0f) {
                        velocidad[i] = 1.0f;
                    } else if (velocidad[i] < -0.3f) {
                        velocidad[i] = -0.3f;
                    }
                }*/
                posicion[0] += velocidad[0] * dT; //m/s * s -> m
                posicion[1] += velocidad[1] * dT; //m/s * s -> m
                posicion[2] += velocidad[2] * dT; //m/s * s -> m

            }
            sElapsed += dT;
        }
        //actualizamos el timestamp
        timestamp = event.timestamp;
        if (sElapsed > 0.3) {
            sElapsed = 0.0f;
        }
    }

    public float[] getVelocidad() {
        return velocidad;
    }

    public float[] getPosicion() {
        return posicion;
    }

    public float[] getGiro() {
        return giro;
    }

    public void resetVelocidad() {
        velocidad = new float[3];
    }

    public void resetPosicion() {
        posicion = new float[3];
    }

    public void resetAndCalibrate() {
        calibVal = new float[3];//reseteamos valores
        calibrate = true;
        calibrationCount = 0;
        resetPosicion();
        resetVelocidad();

    }

    public void setRun(boolean run) {
        if (!this.run && run) {
            resetVelocidad();
            resetPosicion();
            sElapsed = 0;
        }
        this.run = run;
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.rotacion) {

        } else {
            eventoLinearAcel(event);
        }
    }
}
