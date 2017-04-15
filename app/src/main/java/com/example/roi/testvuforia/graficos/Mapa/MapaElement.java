package com.example.roi.testvuforia.graficos.Mapa;

import android.content.Context;

import com.example.roi.testvuforia.R;
import com.example.roi.testvuforia.graficos.ObjLoader.ObjReader;
import com.example.roi.testvuforia.graficos.Shader;
import com.example.roi.testvuforia.graficos.figuras.Obj;

/**
 * Created by roi on 22/12/16.
 */

public class MapaElement {
    String name;
    Obj obj;
    public boolean visible=true;
    public float[] scale={1.0f,1.0f,1.0f};
    public float[] pos= {0.0f,0.0f,0.0f};

    /**
     * Constructor
     * @param name Nombre del elemento del mapa
     * @param obj Obj que contiene el elemento a dibujar
     */
    public MapaElement(String name, Obj obj) {
        this.name = name;
        this.obj = obj;
    }


    public MapaElement(String name, Context context, int resourceId){
        ObjReader objReader = new ObjReader(context, resourceId);
        this.obj = objReader.getObjeto();
    }

    void dibujar(Shader shader){
        if(visible){
            obj.dibujar(shader);
        }
    }
}