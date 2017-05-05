package com.example.roi.climaar.graficos.Mapa;

import com.example.roi.climaar.graficos.Shader;
import com.example.roi.climaar.graficos.figuras.Figura;

import java.io.Serializable;

/**
 * Created by roi on 22/12/16.
 */

public class MapaElement implements Serializable{
    String name;
    Figura figura;
    public boolean visible=true;
    public float[] scale={1.0f,1.0f,1.0f};
    public float[] pos= {0.0f,0.0f,0.0f};


    private int objResourceId;



    /**
     * Constructor
     * @param name Nombre del elemento del mapa
     * @param figura Figura a dibujar
     */
    public MapaElement(String name, Figura figura) {
        this.name = name;
        this.figura = figura;
    }


    void dibujar(Shader shader, float[] modelViewMatrix){
        if(visible){
            if(!figura.isLoaded()) figura.loadFigura();
            figura.dibujar(shader,modelViewMatrix);
        }
    }
}