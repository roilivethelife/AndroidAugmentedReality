package com.example.roi.climaar.modelo.mapa;

import com.example.roi.climaar.modelo.figuras.Figura;
import com.example.roi.climaar.vista.Shader;

import java.io.Serializable;

/**
 * Created by roi on 10/06/17.
 */

public class MapElement implements Serializable{
    String name;
    Figura figura;
    public boolean visible=true;
    public float[] scale={1.0f,1.0f,1.0f};
    public float[] pos= {0.0f,0.0f,0.0f};
    public boolean alignCamera=false;

    public MapElement(MapElement m){
        this.name = m.name;
        figura = m.figura;
        scale = scale.clone();
        pos = pos.clone();
        alignCamera = m.alignCamera;
        visible = m.visible;

    }

    /**
     * Constructor
     * @param name Nombre del elemento del mapa
     * @param figura Figura a dibujar
     */
    public MapElement(String name, Figura figura) {
        this.name = name;
        this.figura = figura;
    }


    public void dibujar(Shader shader, float[] modelViewMatrix){
        if(visible){
            figura.dibujar(shader,modelViewMatrix);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isDynamic() {
        return figura.isDynamic();
    }
}
