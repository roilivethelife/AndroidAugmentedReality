package com.example.roi.climaar.modelo.figuras;

import android.content.Context;

import com.example.roi.climaar.modelo.JsonRest.DynamicMapElement;
import com.example.roi.climaar.modelo.JsonRest.WebRestDataInterface;
import com.example.roi.climaar.modelo.figuras.Texto.GLText;
import com.example.roi.climaar.vista.Shader;

import java.util.Locale;

import static java.lang.Float.NaN;

/**
 * Created by roi on 27/06/17.
 */

public class PanelExterior extends GLText implements DynamicMapElement {

    private static final float TAM_PANEL_X  = 20f;//20cm
    private static final String LOGTAG = "PanelExterior";

    private transient float tempInterior;
    private transient float tempExterior;

    private boolean reloadText;

    private static final String TEXT =
                    "Temp. exterior: %.1f C\n" +
                    "Temp. interior: %.1f C";

    public PanelExterior() {
        super(FigureType.PANEL_EXTERIOR,"Temp. exterior:\n"+
                "Conectando a Servidor...",0,0,true);
        isDynamic=true;
        this.reloadText = false;
        tempExterior=NaN;
        tempInterior=NaN;
        setCentered(true);
    }

    @Override
    public void loadFigura(Context context) {
        super.loadFigura(context);
        setScale(TAM_PANEL_X/getTextLength());
    }

    @Override
    public void dibujar(Shader shader, float[] modelViewMatrix) {
        if(reloadText){
            newText(formatString());
            setScale(TAM_PANEL_X/getTextLength());
            reloadText = false;
        }
        super.dibujar(shader, modelViewMatrix);
    }

    private String formatString(){
        return String.format(Locale.ENGLISH,TEXT,
                tempExterior,
                tempInterior);
    }

    @Override
    public void recargarInformacion(WebRestDataInterface dataInterface) {
        if(!isLoaded()) return;
        boolean cambios = false;
        if (dataInterface.getfTempInterior() != tempInterior) {
            cambios = true;
            tempInterior = dataInterface.getfTempInterior();
        }
        if (dataInterface.getfACtempExterior() != tempExterior) {
            cambios = true;
            tempExterior = dataInterface.getfSRtempConsignaFrio();
        }
        if (cambios) {
            reloadText = true;
        }
    }
}
