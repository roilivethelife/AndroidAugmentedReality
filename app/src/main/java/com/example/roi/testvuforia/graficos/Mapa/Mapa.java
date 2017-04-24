package com.example.roi.testvuforia.graficos.Mapa;

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
    private float tamX;
    private float tamY;
    private float tamZ;

    public Mapa(String nombre,float tamX, float tamY, float tamZ)
    {
        this.nombre=nombre;
        mapaElements = new ArrayList<>();
        if(tamX>0){
            this.tamX=tamX;
        }else this.tamX=0;
        if(tamY>0){
            this.tamY=tamY;
        }else this.tamY=0;
        if(tamZ>0){
            this.tamZ=tamZ;
        }else this.tamZ=0;
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

    public float getTamX() {
        return tamX;
    }

    public float getTamY() {
        return tamY;
    }

    public float getTamZ() {
        return tamZ;
    }
    public void setTam(float tamX, float tamY, float tamZ){
        setTamX(tamX);
        setTamY(tamY);
        setTamZ(tamZ);
    }
    public void setTamX(float tamX) {
        if(tamX>0)
            this.tamX = tamX;
    }

    public void setTamY(float tamY) {
        if(tamY>0)
            this.tamY = tamY;
    }

    public void setTamZ(float tamZ) {
        if(tamZ>0)
            this.tamZ = tamZ;
    }
}

