package com.example.roi.climaar.modelo.figuras;

import android.util.Log;

import com.example.roi.climaar.modelo.JsonRest.DynamicMapElement;
import com.example.roi.climaar.modelo.JsonRest.WebRestDataInterface;
import com.example.roi.climaar.modelo.figuras.Texto.GLText;
import com.example.roi.climaar.vista.Shader;

import java.util.Locale;

import static java.lang.Float.NaN;

/**
 * Created by roi on 27/06/17.
 */

public class PanelTermostato extends GLText implements DynamicMapElement {

    private static final String LOGTAG = "PanelTermostato";
    private int numDespacho;
    private float tempActual;
    private float tempConsignaFrio;
    private float tempConsignaCalor;
    private boolean modoFrio;
    private float tempImpulsionAC;
    private float tempImpulsionSR;

    private boolean reloadText;

    private static final String TEXT =
            "Estado climatizacion - Despacho num. %d\n" +
                    "Temp. actual: %.1f C\n" +
                    "Temp. objetivo: %.1f a %.1f C\n" +
                    "Modo funcionamiento: %s\n" +
                    "Temp. AC: %.1f\n" +
                    "Temp. Suelo radiante: %.1f";
    private static final String MODO_FRIO = "Frio";
    private static final String MODO_CALOR = "Calor";

    public PanelTermostato(int numDespacho) {
        super("",0,0,true);
        isDynamic=true;
        this.numDespacho = numDespacho;
        this.reloadText = false;
        tempActual = NaN;
        tempConsignaFrio = NaN;
        tempConsignaCalor = NaN;
        modoFrio = true;
        tempImpulsionAC = NaN;
        tempImpulsionSR = NaN;
    }

    @Override
    public void dibujar(Shader shader, float[] modelViewMatrix) {
        if(reloadText){
            writeText();
            reloadText = false;
        }
        super.dibujar(shader, modelViewMatrix);
    }

    private void writeText(){
        String modoFrioString = modoFrio? MODO_FRIO : MODO_CALOR;
        newText(String.format(Locale.ENGLISH,TEXT,
                numDespacho,
                tempActual,
                tempConsignaCalor,tempConsignaFrio,
                modoFrioString,
                tempImpulsionAC,
                tempImpulsionSR));
    }


    @Override
    public void recargarInformacion(WebRestDataInterface dataInterface) {
        if(!isLoaded()) return;
        Log.d(LOGTAG,"recargarInformacion(): tempActual="+(int)dataInterface.getfTempInterior());
        boolean cambios = false;
        if (dataInterface.getfTempInterior() != tempActual) {
            cambios = true;
            tempActual = dataInterface.getfTempInterior();
        }

        if (dataInterface.getfSRtempConsignaFrio() != tempConsignaFrio) {
            cambios = true;
            tempConsignaFrio = dataInterface.getfSRtempConsignaFrio();
        }

        if (dataInterface.getfSRtempConsignaCalor() != tempConsignaCalor) {
            cambios = true;
            tempConsignaCalor = dataInterface.getfSRtempConsignaCalor();
        }

        if (dataInterface.isbSRmodoFrio() != modoFrio) {
            cambios = true;
            modoFrio = dataInterface.isbSRmodoFrio();
        }

        if (dataInterface.getfACtempImpulsion() != tempImpulsionAC) {
            cambios = true;
            tempImpulsionAC = dataInterface.getfACtempImpulsion();
        }

        if (dataInterface.getfSRtempImpulsion() != tempImpulsionSR) {
            cambios = true;
            tempImpulsionSR = dataInterface.getfSRtempImpulsion();
        }
        if (cambios) {
            reloadText = true;
        }
    }
}
