package com.example.roi.climaar.modelo.despacho;

import com.example.roi.climaar.modelo.JsonRest.DynamicMapElement;
import com.example.roi.climaar.modelo.figuras.Figura;
import com.example.roi.climaar.vista.Shader;

import java.io.Serializable;

/**
 * Created by roi on 10/06/17.
 */

public class DespachoElement implements Serializable{
    String name;
    Figura figura;
    public boolean visible=true;
    public float[] scale={1.0f,1.0f,1.0f};
    public float[] pos= {0.0f,0.0f,0.0f};
    public boolean alignCamera=false;

    public DespachoElement(DespachoElement m){
        this.name = m.name;
        figura = m.figura;
        scale = scale.clone();
        pos = pos.clone();
        alignCamera = m.alignCamera;
        visible = m.visible;

    }

    /**
     * Constructor
     * @param name Nombre del elemento del despacho
     * @param figura Figura a dibujar
     */
    public DespachoElement(String name, Figura figura) {
        this.name = name;
        this.figura = figura;
    }


    public void dibujar(Shader shader, float[] modelViewMatrix){
        if(visible){
            figura.dibujar(shader,modelViewMatrix);
        }
    }

    private boolean isFiguraNull(){
        return figura==null;
    }

    public void setFigura(Figura figura) {
        this.figura = figura;
    }

    public String getName() {
        return name;
    }

    public boolean isDynamic() {
        return figura.isDynamic();
    }

    public DynamicMapElement getDynamicFigura(){
        if(figura instanceof DynamicMapElement){
            return (DynamicMapElement)figura;
        }else{
            return null;
        }
    }

    public Figura.FigureType getFigureType(){
        if(figura==null) return null;
        else return figura.getType();
    }

    public String getFigureTypeString(){
        if(figura==null) return "Desconocido";
        switch (figura.getType()){
            case OBJ:
                return "Objeto 3D";
            case SUELO_RADIANTE:
                return "Suelo radiante";
            case VENTILADOR:
                return "Ventilador";
            case PANEL_EXTERIOR:
                return "Panel exterior";
            case PANEL_TERMOSTATO:
                return "Panel termostato";
            case TEXT:
                return "Texto";
            default:
                return "Otro";
        }
    }

    public Figura getFigura() {
        return figura;
    }

    public void setName(String name) {
        this.name = name;
    }
}
