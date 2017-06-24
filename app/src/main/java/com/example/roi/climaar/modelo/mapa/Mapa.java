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
    private int numDespacho;
    public ArrayList<MapElement> mapaElements;

    public float[] tam;
    public float[] markerPos;


    public Mapa(Mapa mapa){
        nombre = mapa.nombre;
        descripcion = mapa.descripcion;
        numDespacho = mapa.numDespacho;
        tam = mapa.tam.clone();
        markerPos = mapa.markerPos.clone();
        mapaElements = new ArrayList<>();
        for (MapElement mapElement :
                mapa.mapaElements) {
            mapaElements.add(new MapElement(mapElement));
        }
    }

    public Mapa(String nombre){
        this(nombre,new float[3],new float[3]);
    }

    public Mapa(String nombre, float[] tam, float[] markerPos) {
        this.nombre = nombre;
        mapaElements = new ArrayList<>();
        this.markerPos = markerPos.clone();
        this.tam = tam.clone();
    }

    public void loadFiguras(Context context){
        for (MapElement m : mapaElements) {
            m.figura.loadFigura(context);
        }
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumDespacho() {
        return numDespacho;
    }

    public void setNumDespacho(int numDespacho) {
        this.numDespacho = numDespacho;
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
