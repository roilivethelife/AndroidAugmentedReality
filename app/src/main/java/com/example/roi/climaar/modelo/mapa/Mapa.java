package com.example.roi.climaar.modelo.mapa;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by roi on 11/06/17.
 */

public class Mapa implements Serializable{
    private String nombre;
    private String descripcion;
    public ArrayList<MapElement> mapaElements;

    public Mapa(String nombre, float[] tam, float[] markerPos) {
        this.nombre = nombre;
        mapaElements = new ArrayList<>();
    }

    public void loadFiguras(Context context){
        for (MapElement m : mapaElements) {
            m.figura.loadFigura(context);
        }
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
}
