package com.example.roi.climaar.modelo.figuras;

import android.content.Context;

import com.example.roi.climaar.vista.Shader;

import java.io.Serializable;

/**
 * Created by roi on 22/12/16.
 */

public abstract class Figura implements Serializable{
    protected boolean isDynamic;

    public enum FigureType {
        OBJ, SUELO_RADIANTE, VENTILADOR, TEXT, RECTANGLE, PANEL_EXTERIOR, PANEL_TERMOSTATO
    }

    private FigureType type;

    protected boolean isLoaded = false;

    public boolean isLoaded() {
        return isLoaded;
    }


    public Figura(FigureType type){
        this.type = type;
    }

    
    public FigureType getType() {
        return type;
    }


    public abstract void loadFigura(Context context);

    public abstract void dibujar(Shader shader, float[] modelViewMatrix);

    public boolean isDynamic() {
        return isDynamic;
    }
}
