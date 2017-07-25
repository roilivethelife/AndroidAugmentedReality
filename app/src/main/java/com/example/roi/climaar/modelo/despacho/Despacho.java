package com.example.roi.climaar.modelo.despacho;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by roi on 11/06/17.
 */

public class Despacho implements Serializable{
    private String nombre;
    private String descripcion;
    private int numDespacho;
    public ArrayList<DespachoElement> despachoElements;

    public float[] tam;
    public float[] markerPos;


    public Despacho(Despacho despacho){
        nombre = despacho.nombre;
        descripcion = despacho.descripcion;
        numDespacho = despacho.numDespacho;
        tam = despacho.tam.clone();
        markerPos = despacho.markerPos.clone();
        despachoElements = new ArrayList<>();
        for (DespachoElement despachoElement :
                despacho.despachoElements) {
            despachoElements.add(new DespachoElement(despachoElement));
        }
    }

    public Despacho(String nombre){
        this(nombre,new float[3],new float[3]);
    }

    public Despacho(String nombre, float[] tam, float[] markerPos) {
        this.nombre = nombre;
        despachoElements = new ArrayList<>();
        this.markerPos = markerPos.clone();
        this.tam = tam.clone();
    }

    public void loadFiguras(Context context){
        for (DespachoElement m : despachoElements) {
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
