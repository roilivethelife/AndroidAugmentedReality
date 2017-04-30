package com.example.roi.testvuforia.graficos.Mapa;

import android.util.Log;

import com.example.roi.testvuforia.graficos.AABB;
import com.example.roi.testvuforia.graficos.Shader;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by roi on 14/04/17.
 */

public class Mapa implements Serializable {

    private String nombre;
    private String descripcion;
    public ArrayList<MapaElement> mapaElements;//Elementos del mapa

    //Tamaño habitacion en eje x,y,z
    float[] tam;
    float[] markerPos;


    public Mapa(String nombre,float[] tam, float[] markerPos)
    {
        this.nombre=nombre;
        mapaElements = new ArrayList<>();
        this.tam = tam;
        this.markerPos = markerPos;
        descripcion = "";
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setTam(float[] tam) {
        if(tam.length==3) {
            this.tam = tam;
        }else Log.e("Mapa","Set Tam invalid input lenght, != 3");
    }

    public void setMarkerPos(float[] marketPos) {
        if(marketPos.length==3) {
            this.markerPos = marketPos;
        }else Log.e("Mapa","Set marker por invalid input lenght, != 3");
    }

    public float[] getMarkerPos() {
        return markerPos;
    }

    public float[] getTam() {
        return tam;
    }
}

